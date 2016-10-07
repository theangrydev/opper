/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.early.*;
import io.github.theangrydev.opper.parser.precomputed.nullable.NullableSymbolParseTrees;
import io.github.theangrydev.opper.parser.precomputed.prediction.RulePrediction;
import io.github.theangrydev.opper.parser.precomputed.recursion.RightRecursion;
import io.github.theangrydev.opper.parser.tree.ParseTree;
import io.github.theangrydev.opper.parser.tree.ParseTreeNode;
import io.github.theangrydev.opper.scanner.Location;
import io.github.theangrydev.opper.scanner.ScannedSymbol;
import io.github.theangrydev.opper.scanner.Scanner;

import java.util.List;
import java.util.Optional;

public class EarlyParser implements Parser {

	private final Logger logger;
	private final Grammar grammar;
	private final Scanner scanner;
	private final RulePrediction rulePrediction;
	private final RightRecursion rightRecursion;
	private final NullableSymbolParseTrees nullableSymbolParseTrees;

	private TransitionsEarlySetsBySymbol initialTransitions;
	private TransitionsEarlySetsBySymbol previousTransitions;
	private TransitionsEarlySetsBySymbol currentTransitions;
	private EarlySet currentEarlySet;
	private int currentEarlySetIndex;

	public EarlyParser(Logger logger, Grammar grammar, RightRecursion rightRecursion, RulePrediction rulePrediction, NullableSymbolParseTrees nullableSymbolParseTrees, Scanner scanner) {
		this.logger = logger;
		this.grammar = grammar;
		this.scanner = scanner;
		this.nullableSymbolParseTrees = nullableSymbolParseTrees;
		this.rightRecursion = rightRecursion;
		this.rulePrediction = rulePrediction;
		this.currentEarlySet = new EarlySet(grammar);
	}

	@Override
	public Optional<ParseTree> parse() {
		initialize();
		for (currentEarlySetIndex = 1; scanner.hasNextSymbol(); currentEarlySetIndex++) {
			prepareIteration();
			scanNextSymbol();
			if (currentEarlySet.isEmpty()) {
				logger.log(() -> "Exiting early because the current early set is empty after reading");
				return Optional.empty();
			}
			advanceItemsThatWereWaitingOnCompletions();
			memoizeTransitions();
			debug();
		}
		return currentEarlySet.completedAcceptanceRule(initialTransitions).map(EarlyItem::parseTree).map(ParseTreeNode::firstChild);
	}

	public int finalEarlySetSize() {
		return currentEarlySet.size();
	}

	private void initialize() {
		prepareIteration();
		initialTransitions = currentTransitions;
		previousTransitions = currentTransitions;
		addEarlyItem(new TraditionalEarlyItem(currentTransitions, rulePrediction.initial()));
		memoizeTransitions();
		debug();
	}

	private void prepareIteration() {
		currentEarlySet.reset();
		previousTransitions = currentTransitions;
		currentTransitions = new TransitionsEarlySetsBySymbol(grammar.symbols());
	}

	private void scanNextSymbol() {
		ScannedSymbol scannedSymbol = scanner.nextSymbol();
		Symbol symbol = scannedSymbol.symbol();
		logger.log(() -> "Reading " + symbol);

		Location location = scannedSymbol.location();
		addItemsThatCanAdvanceGivenSymbol(symbol, scannedSymbol.content(), location);
	}

	private void addItemsThatCanAdvanceGivenSymbol(Symbol symbol, String content, Location location) {
		for (EarlyItem itemThatCanAdvance : previousTransitions.itemsThatCanAdvanceGiven(symbol)) {
			addEarlyItem(itemThatCanAdvance.advance(content, location));
		}
	}

	private void advanceItemsThatWereWaitingOnCompletions() {
		for (EarlyItem earlyItem : currentEarlySet) {
			if (earlyItem.isComplete()) {
				for (EarlyItem itemThatCanAdvance : earlyItem.itemsThatCanAdvanceWhenThisIsComplete()) {
					addEarlyItem(itemThatCanAdvance.advance(earlyItem));
				}
			}
		}
	}

	private void memoizeTransitions() {
		for (EarlyItem earlyItem : currentEarlySet) {
			if (!earlyItem.isComplete()) {
				memoizeTransitions(earlyItem);
			}
		}
	}

	private void memoizeTransitions(EarlyItem earlyItem) {
		DottedRule dottedRule = earlyItem.dottedRule();
		Symbol postdot = dottedRule.postDot();
		TransitionsEarlySet transitions = currentTransitions.itemsThatCanAdvanceGiven(postdot);
		if (transitions.hasLeoItem()) {
			return;
		}
		if (isLeoEligible(dottedRule)) {
			transitions.addLeoItem(leoItemToMemoize(earlyItem, dottedRule));
		} else {
			transitions.addEarlyItem(earlyItem);
		}
	}

	private boolean isLeoEligible(DottedRule dottedRule) {
		return rightRecursion.isRightRecursive(dottedRule.rule()) && currentEarlySet.isLeoUnique(dottedRule);
	}

	private LeoItem leoItemToMemoize(EarlyItem earlyItem, DottedRule dottedRule) {
		Optional<LeoItem> predecessor = leoItemPredecessor(dottedRule);
		if (predecessor.isPresent()) {
			return predecessor.get();
		} else {
			return new LeoItem(dottedRule.advance(), earlyItem);
		}
	}

	private Optional<LeoItem> leoItemPredecessor(DottedRule dottedRule) {
		return previousTransitions.itemsThatCanAdvanceGiven(dottedRule.trigger()).leoItem();
	}

	private void addEarlyItem(EarlyItem earlyItem) {
		if (!currentEarlySet.contains(earlyItem)) {
			currentEarlySet.add(earlyItem);
			if (!earlyItem.isComplete()) {
				predict(earlyItem);
			}
		}
	}

	private void predict(EarlyItem earlyItem) {
		List<DottedRule> dottedRules = rulePrediction.rulesThatCanBeTriggeredBy(earlyItem.postDot());
		for (DottedRule predicted : dottedRules) {
			addEarlyItem(new TraditionalEarlyItem(currentTransitions, predicted));
			advanceEarlyItemIfPredictedIsNullable(earlyItem, predicted);
		}
	}

	private void advanceEarlyItemIfPredictedIsNullable(EarlyItem earlyItem, DottedRule predicted) {
		nullableSymbolParseTrees.nullableSymbolParseTree(predicted.trigger()).ifPresent(parseTree -> addEarlyItem(earlyItem.advance(parseTree)));
	}

	private void debug() {
		logger.log(() -> "State at end of iteration #" + currentEarlySetIndex);
		logger.log(() -> "Current Early set: " + currentEarlySet);
		logger.log(() -> "Current transitions: " + currentTransitions);
		logger.log(() -> "");
	}
}

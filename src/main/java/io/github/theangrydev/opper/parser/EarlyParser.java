package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.parser.tree.ParseTree;
import io.github.theangrydev.opper.scanner.Scanner;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.early.*;
import io.github.theangrydev.opper.parser.precomputed.prediction.ComputedRulePrediction;
import io.github.theangrydev.opper.parser.precomputed.prediction.PrecomputedRulePrediction;
import io.github.theangrydev.opper.parser.precomputed.prediction.RulePrediction;
import io.github.theangrydev.opper.parser.precomputed.recursion.ComputedRightRecursion;
import io.github.theangrydev.opper.parser.precomputed.recursion.PrecomputedRightRecursion;
import io.github.theangrydev.opper.parser.precomputed.recursion.RightRecursion;
import io.github.theangrydev.opper.parser.early.EarlySet;
import io.github.theangrydev.opper.parser.early.TransitionsEarlySet;
import io.github.theangrydev.opper.parser.early.TransitionsEarlySetsBySymbol;
import io.github.theangrydev.opper.scanner.ScannedSymbol;

import java.util.Optional;

public class EarlyParser {

	private final Logger logger;
	private final Grammar grammar;
	private final Scanner scanner;
	private final RulePrediction rulePrediction;
	private final RightRecursion rightRecursion;

	private TransitionsEarlySetsBySymbol initialTransitions;
	private TransitionsEarlySetsBySymbol previousTransitions;
	private TransitionsEarlySetsBySymbol currentTransitions;
	private EarlySet currentEarlySet;
	private int currentEarlySetIndex;

	public EarlyParser(Logger logger, Grammar grammar, Scanner scanner) {
		this.logger = logger;
		this.grammar = grammar;
		this.scanner = scanner;
		this.rightRecursion = new PrecomputedRightRecursion(grammar, new ComputedRightRecursion(grammar));
		this.rulePrediction = new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(grammar));
		this.currentEarlySet = new EarlySet(grammar);
	}

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
		return currentEarlySet.completedAcceptanceRule(initialTransitions).map(EarlyItem::parseTree);
	}

	public int finalEarlySetSize() {
		return currentEarlySet.size();
	}

	private void initialize() {
		prepareIteration();
		initialTransitions = currentTransitions;
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
		String content = scannedSymbol.content();
		logger.log(() -> "Reading " + symbol);
		for (EarlyItem itemThatCanAdvance : previousTransitions.itemsThatCanAdvanceGiven(symbol)) {
			addEarlyItem(itemThatCanAdvance.advance(content));
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
			return new LeoItem(dottedRule.advance(), earlyItem.origin());
		}
	}

	private Optional<LeoItem> leoItemPredecessor(DottedRule dottedRule) {
		return previousTransitions.itemsThatCanAdvanceGiven(dottedRule.trigger()).leoItem();
	}

	private void addEarlyItem(EarlyItem earlyItem) {
		currentEarlySet.addIfNew(earlyItem);
		if (!earlyItem.isComplete()) {
			predict(earlyItem);
		}
	}

	private void predict(EarlyItem earlyItem) {
		for (DottedRule predicted : rulePrediction.rulesThatCanBeTriggeredBy(earlyItem.postDot())) {
			currentEarlySet.addIfNew(new TraditionalEarlyItem(currentTransitions, predicted));
		}
	}

	private void debug() {
		logger.log(() -> "State at end of iteration #" + currentEarlySetIndex);
		logger.log(() -> "Current Early set: " + currentEarlySet);
		logger.log(() -> "Current transitions: " + currentTransitions);
		logger.log(() -> "");
	}
}

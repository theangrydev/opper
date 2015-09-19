package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.item.DottedRule;
import io.github.theangrydev.opper.recogniser.item.EarlyItem;
import io.github.theangrydev.opper.recogniser.item.EarlyOrLeoItem;
import io.github.theangrydev.opper.recogniser.item.LeoItem;
import io.github.theangrydev.opper.recogniser.precomputed.prediction.ComputedRulePrediction;
import io.github.theangrydev.opper.recogniser.precomputed.prediction.PrecomputedRulePrediction;
import io.github.theangrydev.opper.recogniser.precomputed.prediction.RulePrediction;
import io.github.theangrydev.opper.recogniser.precomputed.recursion.ComputedRightRecursion;
import io.github.theangrydev.opper.recogniser.precomputed.recursion.PrecomputedRightRecursion;
import io.github.theangrydev.opper.recogniser.precomputed.recursion.RightRecursion;
import io.github.theangrydev.opper.recogniser.progress.EarlySet;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySet;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;
import io.github.theangrydev.opper.recogniser.transition.TransitionsTable;

import java.util.Optional;

public class Recogniser {

	private final Logger logger;
	private final Corpus corpus;
	private final RulePrediction rulePrediction;
	private final RightRecursion rightRecursion;
	private final TransitionsTable transitionsTable;

	private TransitionsEarlySetsBySymbol previousTransitions;
	private TransitionsEarlySetsBySymbol currentTransitions;
	private EarlySet currentEarlySet;
	private int currentEarlySetIndex;

	public Recogniser(Logger logger, Grammar grammar, Corpus corpus) {
		this.logger = logger;
		this.corpus = corpus;
		this.rightRecursion = new PrecomputedRightRecursion(grammar, new ComputedRightRecursion(grammar));
		this.rulePrediction = new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(grammar));
		this.currentEarlySet = new EarlySet(grammar);
		this.transitionsTable = new TransitionsTable(grammar);
	}

	public boolean recognise() {
		initialize();
		for (currentEarlySetIndex = 1; corpus.hasNextSymbol(); currentEarlySetIndex++) {
			prepareIteration();
			read();
			if (currentEarlySet.isEmpty()) {
				logger.log(() -> "Exiting early because the current early set is empty after reading");
				return false;
			}
			reduce();
			debug();
		}
		return currentEarlySet.hasCompletedAcceptanceRule();
	}

	private void initialize() {
		prepareIteration();
		addEarlyItem(rulePrediction.initial(), 0);
		reduce();
		debug();
	}

	private void prepareIteration() {
		transitionsTable.expand();
		currentEarlySet.reset();
		previousTransitions = currentTransitions;
		currentTransitions = transitionsTable.transitionsFromOrigin(currentEarlySetIndex);
	}

	private void read() {
		Symbol symbol = corpus.nextSymbol();
		logger.log(() -> "Reading " + symbol);
		Iterable<EarlyOrLeoItem> predecessors = previousTransitions.forSymbol(symbol);
		for (EarlyOrLeoItem predecessor : predecessors) {
			int origin = predecessor.origin();
			DottedRule next = predecessor.transition(symbol);
			addEarlyItem(next, origin);
		}
	}

	private void reduce() {
		for (EarlyItem earlyItem : currentEarlySet) {
			if (earlyItem.dottedRule().isComplete()) {
				int origin = earlyItem.origin();
				Symbol trigger = earlyItem.trigger();
				reduceOneLeft(origin, trigger);
			}
		}
		memoizeTransitions();
	}

	private void memoizeTransitions() {
		for (EarlyItem earlyItem : currentEarlySet) {
			DottedRule dottedRule = earlyItem.dottedRule();
			if (dottedRule.isComplete()) {
				continue;
			}
			Symbol postdot = dottedRule.postDot();
			TransitionsEarlySet transitions = currentTransitions.forSymbol(postdot);
			if (isLeoEligible(dottedRule)) {
				transitions.add(leoItemToMemoize(earlyItem, dottedRule));
			} else {
				transitions.add(earlyItem);
			}
		}
	}

	private boolean isLeoEligible(DottedRule dottedRule) {
		return rightRecursion.isRightRecursive(dottedRule.rule()) && currentEarlySet.isLeoUnique(dottedRule);
	}

	private LeoItem leoItemToMemoize(EarlyItem earlyItem, DottedRule dottedRule) {
		Optional<LeoItem> predecessor = leoItemPredecessor(dottedRule);
		if (predecessor.isPresent()) {
			return new LeoItem(predecessor.get().dottedRule(), predecessor.get().origin());
		} else {
			return new LeoItem(dottedRule.next(), earlyItem.origin());
		}
	}

	private Optional<LeoItem> leoItemPredecessor(DottedRule dottedRule) {
		return previousTransitions.forSymbol(dottedRule.trigger()).leoItem();
	}

	private void reduceOneLeft(int origin, Symbol left) {
		Iterable<EarlyOrLeoItem> transitionEarlySet = transitionsTable.transitionsFromOrigin(origin).forSymbol(left);
		for (EarlyOrLeoItem item : transitionEarlySet) {
			addEarlyItem(item.transition(left), item.origin());
		}
	}

	private void addEarlyItem(DottedRule confirmed, int origin) {
		currentEarlySet.addIfNew(new EarlyItem(confirmed, origin));
		if (confirmed.isComplete()) {
			return;
		}
		for (DottedRule predicted : rulePrediction.rulesThatCanBeTriggeredBy(confirmed.postDot())) {
			currentEarlySet.addIfNew(new EarlyItem(predicted, currentEarlySetIndex));
		}
	}

	private void debug() {
		logger.log(() -> "State at end of iteration #" + currentEarlySetIndex);
		logger.log(() -> "Current Early set: " + currentEarlySet);
		logger.log(() -> "Transition tables: " + transitionsTable);
	}
}

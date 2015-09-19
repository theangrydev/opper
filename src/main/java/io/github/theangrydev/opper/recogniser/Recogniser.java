package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.item.DottedRule;
import io.github.theangrydev.opper.recogniser.item.EarlyItem;
import io.github.theangrydev.opper.recogniser.item.TraditionalEarlyItem;
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

import java.util.Optional;

public class Recogniser {

	private final Logger logger;
	private final Grammar grammar;
	private final Corpus corpus;
	private final RulePrediction rulePrediction;
	private final RightRecursion rightRecursion;

	private TransitionsEarlySetsBySymbol initialTransitions;
	private TransitionsEarlySetsBySymbol previousTransitions;
	private TransitionsEarlySetsBySymbol currentTransitions;
	private EarlySet currentEarlySet;
	private int currentEarlySetIndex;

	public Recogniser(Logger logger, Grammar grammar, Corpus corpus) {
		this.logger = logger;
		this.grammar = grammar;
		this.corpus = corpus;
		this.rightRecursion = new PrecomputedRightRecursion(grammar, new ComputedRightRecursion(grammar));
		this.rulePrediction = new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(grammar));
		this.currentEarlySet = new EarlySet(grammar);
	}

	public boolean recognise() {
		initialize();
		for (currentEarlySetIndex = 1; corpus.hasNextSymbol(); currentEarlySetIndex++) {
			prepareIteration();
			readNextSymbol();
			if (currentEarlySet.isEmpty()) {
				logger.log(() -> "Exiting early because the current early set is empty after reading");
				return false;
			}
			addItemsThatHaveBeenTriggered();
			memoizeTransitions();
			debug();
		}
		return currentEarlySet.hasCompletedAcceptanceRule(initialTransitions);
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

	private void readNextSymbol() {
		Symbol symbol = corpus.nextSymbol();
		logger.log(() -> "Reading " + symbol);
		for (EarlyItem itemThatCanAdvane : previousTransitions.itemsThatCanAdvanceGiven(symbol)) {
			addEarlyItem(itemThatCanAdvane.advance());
		}
	}

	private void addItemsThatHaveBeenTriggered() {
		for (EarlyItem earlyItem : currentEarlySet) {
			if (earlyItem.isComplete()) {
				for (EarlyItem itemThatCanAdvane : earlyItem.itemsThatCanAdvanceWhenThisIsComplete()) {
					addEarlyItem(itemThatCanAdvane.advance());
				}
			}
		}
	}

	private void memoizeTransitions() {
		for (EarlyItem earlyItem : currentEarlySet) {
			if (earlyItem.isComplete()) {
				continue;
			}
			DottedRule dottedRule = earlyItem.dottedRule();
			Symbol postdot = dottedRule.postDot();
			TransitionsEarlySet transitions = currentTransitions.itemsThatCanAdvanceGiven(postdot);
			if (isLeoEligible(dottedRule)) {
				transitions.addLeoItem(leoItemToMemoize(earlyItem, dottedRule));
			} else {
				transitions.addEarlyItem(earlyItem);
			}
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
		if (earlyItem.isComplete()) {
			return;
		}
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

package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.prediction.ComputedRulePrediction;
import io.github.theangrydev.opper.recogniser.prediction.PrecomputedRulePrediction;
import io.github.theangrydev.opper.recogniser.prediction.RulePrediction;
import io.github.theangrydev.opper.recogniser.recursion.ComputedRightRecursion;
import io.github.theangrydev.opper.recogniser.recursion.PrecomputedRightRecursion;
import io.github.theangrydev.opper.recogniser.recursion.RightRecursion;

import java.util.Set;
import java.util.function.Predicate;

import static io.github.theangrydev.opper.common.Streams.stream;

public class Recogniser {

	private final Logger logger;
	private final Grammar grammar;
	private final Corpus corpus;
	private final RulePrediction rulePrediction;
	private final RightRecursion rightRecursion;
	private final EarlyItemFactory earlyItemFactory;
	private final TransitionTables transitionTables;
	private final EarlySetsTable earlySetsTable;
	private int currentEarlySetIndex;

	public Recogniser(Logger logger, Grammar grammar, Corpus corpus) {
		this.logger = logger;
		this.grammar = grammar;
		this.corpus = corpus;
		this.rightRecursion = new PrecomputedRightRecursion(grammar, new ComputedRightRecursion(grammar));
		this.rulePrediction = new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(grammar));
		this.earlyItemFactory = new EarlyItemFactory();
		this.earlySetsTable = new EarlySetsTable();
		this.transitionTables = new TransitionTables(grammar);
	}

	private void debug() {
		logger.log(() -> "");
		logger.log(() -> "State at end of iteration #" + currentEarlySetIndex);
		logger.log(() -> "Early set [" + currentEarlySetIndex + "]: " + currentEarlySet());
		logger.log(() -> "Transition tables: " + transitionTables);
		logger.log(() -> "");
	}

	// Algorithm 1
	public boolean recognise() {
		initialize();
		for (currentEarlySetIndex = 1; corpus.hasNextSymbol(); currentEarlySetIndex++) {
			expand();
			read();
			if (currentEarlySet().isEmpty()) {
				logger.log(() -> "The early set was empty after scanning, failing the recognise.");
				return false;
			}
			reduce();
			debug();
		}
		return lastEarlySetHasCompletedAcceptanceRule();
	}

	private boolean lastEarlySetHasCompletedAcceptanceRule() {
		return stream(earlySetsTable.lastEntry()).anyMatch(hasCompletedAcceptanceRule());
	}

	private Predicate<EarlyItem> hasCompletedAcceptanceRule() {
		return earlyItem -> earlyItem.hasCompletedAcceptanceRule(grammar.acceptanceSymbol());
	}

	private void expand() {
		transitionTables.expand();
		earlySetsTable.expand();
	}

	// Algorithm 2
	private void initialize() {
		expand();
		addEarlyItem(DottedRule.begin(grammar.acceptanceRule()), 0);
		reduce();
		debug();
	}

	// Algorithm 3
	private void read() {
		logger.log(() -> "Read #" + currentEarlySetIndex);
		Symbol symbol = corpus.nextSymbol();
		logger.log(() -> "Processing symbol: " + symbol);
		Set<EarlyOrLeoItem> predecessors = previousTransitionsEarlySet(symbol);
		logger.log(() -> "Predecessors in transition early set #" + (currentEarlySetIndex - 1) + ": " + predecessors);
		for (EarlyOrLeoItem predecessor : predecessors) {
			int origin = predecessor.origin();
			logger.log(() -> "origin: " + origin);
			DottedRule next = predecessor.transition(symbol);
			logger.log(() -> "next: " + next);
			addEarlyItem(next, origin);
		}
	}

	// Algorithm 4
	private void reduce() {
		logger.log(() -> "Reduce #" + currentEarlySetIndex);
		for (EarlyItem earlyItem : currentEarlySet()) {
			logger.log(() -> "Processing: " + earlyItem);
			int origin = earlyItem.origin();
			Symbol trigger = earlyItem.trigger();
			logger.log(() -> "Trigger of rule: " + trigger);
			reduceOneLeft(origin, trigger);
		}
		memoizeTransitions();
	}

	// Algorithm 5
	private void memoizeTransitions() {
		for (EarlyItem earlyItem : currentEarlySet()) {
			DottedRule dottedRule = earlyItem.dottedRule();
			if (dottedRule.isComplete()) {
				continue;
			}
			Symbol postdot = dottedRule.postDot();
			Set<EarlyOrLeoItem> transitions = currentTransitionsEarlySet(postdot);
			if (isLeoEligible(dottedRule)) {
				transitions.clear();
				logger.log(() -> dottedRule + " is Leo eligible for " + postdot);
				transitions.add(new LeoItem(dottedRule, dottedRule.penult().get(), earlyItem.origin()));
			} else {
				logger.log(() -> dottedRule + " is not Leo eligible for " + postdot);
				transitions.add(earlyItem);
			}
		}
	}

	// Algorithm 6
	private void reduceOneLeft(int origin, Symbol left) {
		logger.log(() -> "Reduce one");
		Set<EarlyOrLeoItem> transitionEarlySet = transitionEarlySet(origin, left);
		logger.log(() -> "Origin transitions[" + origin + "," + left + "]: " + transitionEarlySet);
		for (EarlyOrLeoItem item : transitionEarlySet) {
			logger.log(() -> "Reducing " + item);
			performEarlyReduction(left, item);
		}
	}

	private void performEarlyReduction(Symbol left, EarlyOrLeoItem item) {
		// Algorithm 7 and 8 are handled using polymorphism
		DottedRule next = item.transition(left);
		int origin = item.origin();
		addEarlyItem(next, origin);
	}

	// Algorithm 9
	private void addEarlyItem(DottedRule confirmed, int origin) {
		logger.log(() -> "Adding early item to set #" + currentEarlySetIndex + " with rule " + confirmed + " and origin " + origin);
		EarlyItem confirmedEarlyItem = earlyItemFactory.createEarlyItem(confirmed, origin);
		EarlySet earlySet = currentEarlySet();
		addEarlyItemIfItIsNew(earlySet, confirmedEarlyItem);
		if (confirmed.isComplete()) {
			logger.log(() -> "Dotted rule is complete, not doing any predictions.");
			return;
		}
		logger.log(() -> "Making predictions based on the postdot symbol: " + confirmed.postDot());
		for (Rule rule : rulePrediction.rulesThatCanBeTriggeredBy(confirmed.postDot())) {
			EarlyItem predictedEarlyItem = earlyItemFactory.createEarlyItem(rule, currentEarlySetIndex);
			addEarlyItemIfItIsNew(earlySet, predictedEarlyItem);
		}
	}

	private void addEarlyItemIfItIsNew(EarlySet earlySet, EarlyItem earlyItem) {
		if (isNew(earlyItem)) {
			logger.log(() -> "Early item '" + earlyItem + "' is new, adding it...");
			earlySet.add(earlyItem);
		} else {
			logger.log(() -> "Early item '" + earlyItem + "' is not new, ignored it.");
		}
	}

	private boolean isLeoEligible(DottedRule dottedRule) {
		return rightRecursion.isRightRecursive(dottedRule.rule()) && currentEarlySet().isLeoUnique(dottedRule);
	}

	private boolean isNew(EarlyItem earlyItem) {
		return earlySet(earlyItem.origin()).isNew(currentEarlySetIndex, earlyItem);
	}

	private EarlySet earlySet(int earlySetIndex) {
		return earlySetsTable.earlySet(earlySetIndex);
	}

	private EarlySet currentEarlySet() {
		return earlySet(currentEarlySetIndex);
	}

	private Set<EarlyOrLeoItem> transitionEarlySet(int earlySetIndex, Symbol symbol) {
		return transitionTables.transitions(symbol, earlySetIndex);
	}

	private Set<EarlyOrLeoItem> currentTransitionsEarlySet(Symbol postdot) {
		return transitionEarlySet(currentEarlySetIndex, postdot);
	}

	private Set<EarlyOrLeoItem> previousTransitionsEarlySet(Symbol symbol) {
		return transitionEarlySet(currentEarlySetIndex - 1, symbol);
	}
}

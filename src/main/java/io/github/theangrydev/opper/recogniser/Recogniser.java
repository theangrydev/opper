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

import java.util.Optional;
import java.util.function.Predicate;

import static io.github.theangrydev.opper.common.Streams.stream;

public class Recogniser {

	private final Logger logger;
	private final Grammar grammar;
	private final Corpus corpus;
	private final RulePrediction rulePrediction;
	private final RightRecursion rightRecursion;
	private final DottedRuleFactory dottedRuleFactory;
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
		this.dottedRuleFactory = new DottedRuleFactory(grammar);
		this.earlyItemFactory = new EarlyItemFactory(dottedRuleFactory);
		this.earlySetsTable = new EarlySetsTable();
		this.transitionTables = new TransitionTables(grammar);
	}

	private void debug() {
		logger.log(() -> "State at end of iteration #" + currentEarlySetIndex);
		logger.log(() -> "Early sets: " + earlySetsTable);
		logger.log(() -> "Transition tables: " + transitionTables);
	}

	public boolean recognise() {
		initialize();
		for (currentEarlySetIndex = 1; corpus.hasNextSymbol(); currentEarlySetIndex++) {
			expand();
			read();
			if (currentEarlySet().isEmpty()) {
				logger.log(() -> "Exiting early because the current early set is empty after reading");
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

	private void initialize() {
		expand();
		addEarlyItem(dottedRuleFactory.begin(grammar.acceptanceRule()), 0);
		reduce();
		debug();
	}

	private void read() {
		Symbol symbol = corpus.nextSymbol();
		logger.log(() -> "Reading " + symbol);
		Iterable<EarlyOrLeoItem> predecessors = previousTransitionsEarlySet(symbol);
		for (EarlyOrLeoItem predecessor : predecessors) {
			int origin = predecessor.origin();
			DottedRule next = predecessor.transition(symbol);
			addEarlyItem(next, origin);
		}
	}

	private void reduce() {
		for (EarlyItem earlyItem : currentEarlySet()) {
			if (earlyItem.dottedRule().isComplete()) {
				int origin = earlyItem.origin();
				Symbol trigger = earlyItem.trigger();
				reduceOneLeft(origin, trigger);
			}
		}
		memoizeTransitions();
	}

	private void memoizeTransitions() {
		for (EarlyItem earlyItem : currentEarlySet()) {
			DottedRule dottedRule = earlyItem.dottedRule();
			if (dottedRule.isComplete()) {
				continue;
			}
			Symbol postdot = dottedRule.postDot();
			TransitionsEarlySet transitions = currentTransitionsEarlySet(postdot);
			if (isLeoEligible(dottedRule)) {
				transitions.add(leoItemToMemoize(earlyItem, dottedRule));
			} else {
				transitions.add(earlyItem);
			}
		}
	}

	private LeoItem leoItemToMemoize(EarlyItem earlyItem, DottedRule dottedRule) {
		Optional<EarlyOrLeoItem> predecessor = leoItemPredecessor(dottedRule);
		if (predecessor.isPresent()) {
			return new LeoItem(predecessor.get().dottedRule(), dottedRule.penult().get(), predecessor.get().origin());
		} else {
			return new LeoItem(dottedRule.next(), dottedRule.penult().get(), earlyItem.origin());
		}
	}

	private Optional<EarlyOrLeoItem> leoItemPredecessor(DottedRule dottedRule) {
		return previousTransitionsEarlySet(dottedRule.trigger()).leoItem();
	}

	private void reduceOneLeft(int origin, Symbol left) {
		Iterable<EarlyOrLeoItem> transitionEarlySet = transitionEarlySet(origin, left);
		for (EarlyOrLeoItem item : transitionEarlySet) {
			performEarlyReduction(left, item);
		}
	}

	private void performEarlyReduction(Symbol left, EarlyOrLeoItem item) {
		DottedRule next = item.transition(left);
		int origin = item.origin();
		addEarlyItem(next, origin);
	}

	private void addEarlyItem(DottedRule confirmed, int origin) {
		EarlyItem confirmedEarlyItem = earlyItemFactory.createEarlyItem(confirmed, origin);
		EarlySet earlySet = currentEarlySet();
		earlySet.addIfNew(confirmedEarlyItem);
		if (confirmed.isComplete()) {
			return;
		}
		for (Rule rule : rulePrediction.rulesThatCanBeTriggeredBy(confirmed.postDot())) {
			EarlyItem predictedEarlyItem = earlyItemFactory.createEarlyItem(rule, currentEarlySetIndex);
			earlySet.addIfNew(predictedEarlyItem);
		}
	}

	private boolean isLeoEligible(DottedRule dottedRule) {
		return rightRecursion.isRightRecursive(dottedRule.rule()) && currentEarlySet().isLeoUnique(dottedRule);
	}

	private EarlySet earlySet(int earlySetIndex) {
		return earlySetsTable.earlySet(earlySetIndex);
	}

	private EarlySet currentEarlySet() {
		return earlySet(currentEarlySetIndex);
	}

	private TransitionsEarlySet transitionEarlySet(int earlySetIndex, Symbol symbol) {
		return transitionTables.transitions(symbol, earlySetIndex);
	}

	private TransitionsEarlySet currentTransitionsEarlySet(Symbol postdot) {
		return transitionEarlySet(currentEarlySetIndex, postdot);
	}

	private TransitionsEarlySet previousTransitionsEarlySet(Symbol symbol) {
		return transitionEarlySet(currentEarlySetIndex - 1, symbol);
	}
}

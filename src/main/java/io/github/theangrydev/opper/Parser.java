package io.github.theangrydev.opper;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static io.github.theangrydev.opper.Streams.stream;

public class Parser {

	private final Logger logger;
	private final Grammar grammar;
	private final Corpus corpus;
	private final RulePrediction rulePrediction;
	private final EarlyItemFactory earlyItemFactory;
	private final TransitionTables transitionTables;
	private final EarlySetsTable earlySetsTable;

	public Parser(Logger logger, Grammar grammar, Corpus corpus) {
		this.logger = logger;
		this.grammar = grammar;
		this.corpus = corpus;
		this.rulePrediction = new CachedRulePrediction(grammar, new ComputedRulePrediction(logger, grammar));
		this.earlyItemFactory = new EarlyItemFactory();
		this.earlySetsTable = new EarlySetsTable();
		this.transitionTables = new TransitionTables(grammar);
	}

	private void debug(int i) {
		logger.log(() -> "");
		logger.log(() -> "State at end of iteration #" + i);
		logger.log(() -> "Early set [" + i + "]: " + earlySet(i));
		logger.log(() -> "Transition tables: " + transitionTables);
		logger.log(() -> "");
	}

	// Algorithm 1
	public boolean parse() {
		initialize();
		for (int i = 1; corpus.hasNextSymbol(); i++) {
			expand();
			scan(i);
			if (earlySet(i).isEmpty()) {
				logger.log(() -> "The early set was empty after scanning, failing the parse.");
				return false;
			}
			reduce(i);
			debug(i);
		}
		return lastEarlySetHasCompletedAcceptanceRule();
	}

	private boolean lastEarlySetHasCompletedAcceptanceRule() {
		return stream(earlySetsTable.lastEntry()).filter(hasCompletedAcceptanceRule()).findFirst().isPresent();
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
		addEarlyItem(0, DottedRule.begin(grammar.acceptanceRule()), 0);
		reduce(0);
		debug(0);
	}

	// Algorithm 3
	private void scan(int i) {
		logger.log(() -> "Scan #" + i);
		Symbol symbol = corpus.nextSymbol();
		logger.log(() -> "Processing symbol: " + symbol);
		Set<EarlyOrLeoItem> predecessors = transitionEarlySet(i - 1, symbol);
		logger.log(() -> "Predecessors in transition early set #" + (i - 1) + ": " + predecessors);
		for (EarlyOrLeoItem predecessor : predecessors) {
			int origin = predecessor.origin();
			logger.log(() -> "origin: " + origin);
			DottedRule next = predecessor.transition(symbol);
			logger.log(() -> "next: " + next);
			addEarlyItem(i, next, origin);
		}
	}

	// Algorithm 4
	private void reduce(int i) {
		logger.log(() -> "Reduce #" + i);
		for (EarlyItem earlyItem : earlySet(i)) {
			logger.log(() -> "Processing: " + earlyItem);
			int origin = earlyItem.origin();
			Optional<Symbol> left = earlyItem.leftOfCompletedRule();
			logger.log(() -> "Left of completed rule: " + left);
			if (left.isPresent()) {
				reduceOneLeft(i, origin, left.get());
			}
		}
		memoizeTransitions(i);
	}

	// Algorithm 5
	private void memoizeTransitions(int i) {
		for (EarlyItem earlyItem : earlySet(i)) {
			DottedRule dottedRule = earlyItem.dottedRule();
			if (dottedRule.isComplete()) {
				continue;
			}
			Symbol postdot = dottedRule.postDot();
			Set<EarlyOrLeoItem> transitions = transitionEarlySet(i, postdot);
			if (postdotTransitionIsUniqueByRightRecursiveRule(dottedRule)) {
				transitions.add(new LeoItem(dottedRule, postdot, i));
			} else {
				transitions.add(earlyItem);
			}
		}
	}

	// Algorithm 6
	private void reduceOneLeft(int i, int origin, Symbol left) {
		logger.log(() -> "Reduce one");
		Set<EarlyOrLeoItem> transitionEarlySet = transitionEarlySet(origin, left);
		logger.log(() -> "Origin transitions[" + origin + "," + left + "]: " + transitionEarlySet);
		for (EarlyOrLeoItem item : transitionEarlySet) {
			performEarlyReduction(i, left, item);
		}
	}

	private void performEarlyReduction(int i, Symbol left, EarlyOrLeoItem item) {
		// Algorithm 7 and 8 are handled using polymorphism
		DottedRule next = item.transition(left);
		int origin = item.origin();
		addEarlyItem(i, next, origin);
	}

	// Algorithm 9
	private void addEarlyItem(int i, DottedRule confirmed, int origin) {
		logger.log(() -> "Adding early item to set #" + i + " with rule " + confirmed + " and origin " + origin);
		EarlyItem confirmedEarlyItem = earlyItemFactory.createEarlyItem(confirmed, origin);
		EarlySet earlySet = earlySet(i);
		addEarlyItemIfItIsNew(i, earlySet, confirmedEarlyItem);
		if (confirmed.isComplete()) {
			logger.log(() -> "Dotted rule is complete, not doing any predictions.");
			return;
		}
		logger.log(() -> "Making predictions based on the postdot symbol: " + confirmed.postDot());
		for (Rule rule : rulePrediction.predict(confirmed.postDot())) {
			EarlyItem earlyItem = earlyItemFactory.createEarlyItem(rule, i);
			addEarlyItemIfItIsNew(i, earlySet, earlyItem);
		}
	}

	private void addEarlyItemIfItIsNew(int i, EarlySet earlySet, EarlyItem earlyItem) {
		if (isNew(i, earlyItem)) {
			logger.log(() -> "Early item is new, adding it...");
			earlySet.add(earlyItem);
		} else {
			logger.log(() -> "Early item is not new, ignored it.");
		}
	}

	private boolean postdotTransitionIsUniqueByRightRecursiveRule(DottedRule postdot) {
		return false;
	}

	private boolean isNew(int i, EarlyItem earlyItem) {
		return earlySet(earlyItem.origin()).isNew(i, earlyItem);
	}

	private EarlySet earlySet(int i) {
		return earlySetsTable.earlySet(i);
	}

	private Set<EarlyOrLeoItem> transitionEarlySet(int i, Symbol symbol) {
		return transitionTables.transitions(symbol, i);
	}
}

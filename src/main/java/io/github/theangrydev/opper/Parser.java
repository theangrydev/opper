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
	private int currentEarlySetIndex;

	public Parser(Logger logger, Grammar grammar, Corpus corpus) {
		this.logger = logger;
		this.grammar = grammar;
		this.corpus = corpus;
		this.rulePrediction = new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(logger, grammar));
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
	public boolean parse() {
		initialize();
		for (currentEarlySetIndex = 1; corpus.hasNextSymbol(); currentEarlySetIndex++) {
			expand();
			scan();
			if (currentEarlySet().isEmpty()) {
				logger.log(() -> "The early set was empty after scanning, failing the parse.");
				return false;
			}
			reduce();
			debug();
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
		addEarlyItem(DottedRule.begin(grammar.acceptanceRule()), 0);
		reduce();
		debug();
	}

	// Algorithm 3
	private void scan() {
		logger.log(() -> "Scan #" + currentEarlySetIndex);
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
			Optional<Symbol> left = earlyItem.leftOfCompletedRule();
			logger.log(() -> "Left of completed rule: " + left);
			if (left.isPresent()) {
				reduceOneLeft(origin, left.get());
			}
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
			transitions.add(earlyItem);
		}
	}

	// Algorithm 6
	private void reduceOneLeft(int origin, Symbol left) {
		logger.log(() -> "Reduce one");
		Set<EarlyOrLeoItem> transitionEarlySet = transitionEarlySet(origin, left);
		logger.log(() -> "Origin transitions[" + origin + "," + left + "]: " + transitionEarlySet);
		for (EarlyOrLeoItem item : transitionEarlySet) {
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
		for (Rule rule : rulePrediction.predict(confirmed.postDot())) {
			EarlyItem earlyItem = earlyItemFactory.createEarlyItem(rule, currentEarlySetIndex);
			addEarlyItemIfItIsNew(earlySet, earlyItem);
		}
	}

	private void addEarlyItemIfItIsNew(EarlySet earlySet, EarlyItem earlyItem) {
		if (isNew(earlyItem)) {
			logger.log(() -> "Early item is new, adding it...");
			earlySet.add(earlyItem);
		} else {
			logger.log(() -> "Early item is not new, ignored it.");
		}
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

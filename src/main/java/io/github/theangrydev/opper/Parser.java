package io.github.theangrydev.opper;

import java.util.Optional;
import java.util.Set;

public class Parser {

	private final Grammar grammar;
	private final Corpus corpus;
	private final RulePrediction rulePrediction;
	private final EarlyItemFactory earlyItemFactory;
	private final TransitionTables transitionTables;
	private final EarlySetsTable earlySetsTable;

	public Parser(Grammar grammar, Corpus corpus) {
		this.grammar = grammar;
		this.corpus = corpus;
		this.rulePrediction = new ComputedRulePrediction(grammar);
		this.earlyItemFactory = new EarlyItemFactory();
		this.earlySetsTable = new EarlySetsTable();
		this.transitionTables = new TransitionTables(grammar);
	}

	private void debug(int i) {
		System.out.println("");
		System.out.println("State at end of iteration #" + i);
		System.out.println("Early set [" + i + "]: " + earlySet(i));
		System.out.println("Transition tables: " + transitionTables);
		System.out.println("");
	}

	// Algorithm 1
	public boolean parse() {
		initialize();
		for (int i = 1; corpus.hasNextSymbol(); i++) {
			expand();
			scan(i);
			if (earlySet(i).isEmpty()) {
				System.out.println("The early set was empty after scanning, failing the parse.");
				return false;
			}
			reduce(i);
			debug(i);
		}
		for (EarlyItem earlyItem : earlySetsTable.lastEntry()) {
			if (earlyItem.canAccept(grammar.acceptanceSymbol())) {
				return true;
			}
		}
		return false;
	}

	private void expand() {
		transitionTables.expand();
		earlySetsTable.expand();
	}

	// Algorithm 2
	private void initialize() {
		expand();
		addEarlyItem(0, DottedRule.begin(grammar.startRule()), 0);
		reduce(0);
		debug(0);
	}

	// Algorithm 3
	private void scan(int i) {
		System.out.println("Scan #" + i);
		Symbol symbol = corpus.nextSymbol();
		System.out.println("Processing symbol: " + symbol);
		Set<EarlyOrLeoItem> predecessors = transitionEarlySet(i - 1, symbol);
		System.out.println("Predecessors in transition early set #" + (i - 1) + ": " + predecessors);
		for (EarlyOrLeoItem predecessor : predecessors) {
			int origin = predecessor.origin();
			System.out.println("origin: " + origin);
			DottedRule next = predecessor.transition(symbol);
			System.out.println("next: " + next);
			addEarlyItem(i, next, origin);
		}
	}

	// Algorithm 4
	private void reduce(int i) {
		System.out.println("Reduce #" + i);
		for (EarlyItem earlyItem : earlySet(i)) {
			System.out.println("Processing: " + earlyItem);
			int origin = earlyItem.origin();
			Optional<Symbol> left = earlyItem.leftOfCompletedRules();
			System.out.println("Left of completed rule: " + left);
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
		System.out.println("Reduce one");
		Set<EarlyOrLeoItem> transitionEarlySet = transitionEarlySet(origin, left);
		System.out.println("Origin transitions[" + origin + "," + left + "]: " + transitionEarlySet);
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
	private void addEarlyItem(int earlySetIndex, DottedRule confirmed, int origin) {
		System.out.println("Adding early item to set #" + earlySetIndex + " with rule " + confirmed + " and origin " + origin);
		EarlyItem confirmedEarlyItem = earlyItemFactory.createEarlyItem(confirmed, origin);
		EarlySet earlySet = earlySet(earlySetIndex);
		if (isNew(earlySetIndex, confirmedEarlyItem)) {
			System.out.println("Early item is new, adding it...");
			earlySet.add(earlySetIndex, confirmedEarlyItem);
		} else {
			System.out.println("Early item is not new, ignored it.");
		}
		if (confirmed.isComplete()) {
			System.out.println("Dotted rule is complete, not doing any predictions.");
			return;
		}
		System.out.println("Making predictions based on the postdot symbol: " + confirmed.postDot());
		for (Rule rule : rulePrediction.predict(confirmed.postDot())) {
			earlySet.add(earlySetIndex, earlyItemFactory.createEarlyItem(rule, earlySetIndex));
		}
	}

	private boolean postdotTransitionIsUniqueByRightRecursiveRule(DottedRule postdot) {
		return false;
	}

	private boolean isNew(int earlySetIndex, EarlyItem earlyItem) {
		return earlySetIndex == 0 || earlySet(earlySetIndex - 1).isNew(earlySetIndex, earlyItem);
	}

	private EarlySet earlySet(int i) {
		return earlySetsTable.earlySet(i);
	}

	private Set<EarlyOrLeoItem> transitionEarlySet(int i, Symbol symbol) {
		return transitionTables.transitions(symbol, i);
	}
}

package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Parser {

	private final Grammar grammar;
	private final Corpus corpus;
	private final EarlyItemFactory earlyItemFactory;
	private final TransitionTables transitionTables;
	private final EarlySetsTable earlySetsTable;

	public Parser(Grammar grammar, Corpus corpus) {
		this.grammar = grammar;
		this.corpus = corpus;
		this.earlyItemFactory = new EarlyItemFactory();
		this.earlySetsTable = new EarlySetsTable(corpus.size());
		this.transitionTables = new TransitionTables(grammar.symbols(), corpus.size());
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
		reduce(0);
		debug(0);
		for (int i = 1; i <= corpus.size(); i++) {
			scan(i);
			if (earlySet(i).isEmpty()) {
				System.out.println("The early set was empty after scanning, failing the parse.");
				return false;
			}
			reduce(i);
			debug(i);
		}
		for (EarlyItem earlyItem : earlySet(corpus.size())) {
			if (earlyItem.canAccept(grammar.acceptanceSymbol())) {
				return true;
			}
		}
		return false;
	}

	// Algorithm 2
	private void initialize() {
		addEarlyItem(0, new DottedRule(grammar.startRule(), 0), 0);
	}

	// Algorithm 3
	private void scan(int i) {
		System.out.println("Scan #" + i);
		Symbol symbol = corpus.symbol(i - 1);
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
	private void reduceOneLeft(int location, int origin, Symbol left) {
		System.out.println("Reduce one");
		Set<EarlyOrLeoItem> transitionEarlySet = transitionEarlySet(origin, left);
		System.out.println("Origin transitions[" + origin + "," + left + "]: " + transitionEarlySet);
		for (EarlyOrLeoItem item : transitionEarlySet) {
			// Algorithm 7 and 8 are handled using polymorphism
			DottedRule next = item.transition(left);
			addEarlyItem(location, next, origin);
		}
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

		// prediction
		// aim is to find rules with LHS that is the prefix of any string that can be derived from the postdot symbol
		List<Symbol> derivationPrefixes = determineDerivationPrefixes(confirmed.postDot());
		System.out.println("Derivation prefixes are: " + derivationPrefixes);
		List<Rule> applicableRules = determineApplicableRules(derivationPrefixes);
		System.out.println("Applicable rules are: " + applicableRules);
		for (Rule rule : applicableRules) {
			earlySet.add(earlySetIndex, earlyItemFactory.createEarlyItem(new DottedRule(rule, 0), earlySetIndex));
		}
	}

	private List<Rule> determineApplicableRules(List<Symbol> derivationPrefixes) {
		List<Rule> applicableRules = new ObjectArrayList<>();
		for (Symbol derivationPrefix : derivationPrefixes) {
			for (Rule rule : grammar.rules()) {
				if (rule.left().equals(derivationPrefix)) {
					applicableRules.add(rule);
				}
			}
		}
		return applicableRules;
	}

	// this could be precomputed per symbol
	private List<Symbol> determineDerivationPrefixes(Symbol symbol) {
		List<Symbol> derivations = new ObjectArrayList<>();
		derivations.add(symbol);
		boolean changed;
		int i = 0;
		do {
			int size = derivations.size();
			changed = false;
			for (; i < size; i++) {
				for (Rule rule : grammar.rules()) {
					if (rule.left().equals(derivations.get(i))) {
						Symbol prefix = rule.symbolAt(0);
						derivations.add(prefix);
						changed = true;
					}
				}
			}
		} while (changed);
		return derivations;
	}

	private boolean postdotTransitionIsUniqueByRightRecursiveRule(DottedRule postdot) {
		return false;
	}

	private boolean isNew(int earlySetIndex, EarlyItem earlyItem) {
		return earlySetIndex == 0 || earlySet(earlySetIndex - 1).isNew(earlySetIndex, earlyItem);
	}

	private EarlySet earlySet(int location) {
		return earlySetsTable.earlySet(location);
	}

	private Set<EarlyOrLeoItem> transitionEarlySet(int location, Symbol symbol) {
		return transitionTables.transitions(symbol, location);
	}
}

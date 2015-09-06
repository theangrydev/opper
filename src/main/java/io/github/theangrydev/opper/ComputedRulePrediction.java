package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class ComputedRulePrediction implements RulePrediction {

	private final Grammar grammar;

	public ComputedRulePrediction(Grammar grammar) {
		this.grammar = grammar;
	}

	@Override
	public List<Rule> predict(Symbol symbol) {
		// prediction
		// aim is to find rules with LHS that is the prefix of any string that can be derived from the postdot symbol
		List<Symbol> derivationPrefixes = determineDerivationPrefixes(symbol);
		System.out.println("Derivation prefixes are: " + derivationPrefixes);
		List<Rule> applicableRules = determineApplicableRules(derivationPrefixes);
		System.out.println("Applicable rules are: " + applicableRules);
		return applicableRules;
	}

	private List<Rule> determineApplicableRules(List<Symbol> derivationPrefixes) {
		return grammar.rules().stream().filter(leftIsIn(derivationPrefixes)).collect(toList());
	}

	private Predicate<Rule> leftIsIn(List<Symbol> derivationPrefixes) {
		return rule -> derivationPrefixes.contains(rule.left());
	}

	// this could be precomputed per symbol
	private List<Symbol> determineDerivationPrefixes(Symbol symbol) {
		List<Symbol> derivations = new ObjectArrayList<>();
		derivations.add(symbol);
		for (int i = 0; i < derivations.size(); i++) {
			for (Rule rule : grammar.rules()) {
				if (rule.left().equals(derivations.get(i))) {
					Symbol prefix = rule.symbolAt(0);
					derivations.add(prefix);
				}
			}
		}
		return derivations;
	}
}

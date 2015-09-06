package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

	private Predicate<Rule> leftIsEqualTo(Symbol symbol) {
		return rule -> symbol.equals(rule.left());
	}

	// this could be precomputed per symbol
	private List<Symbol> determineDerivationPrefixes(Symbol symbol) {
		List<Symbol> derivationsPrefixes = new ObjectArrayList<>();
		Set<Symbol> uniqueDerivations = new ObjectArraySet<>();
		derivationsPrefixes.add(symbol);
		uniqueDerivations.add(symbol);
		for (Symbol derivationPrefix : derivationsPrefixes) {
			derivationPrefixes(derivationPrefix).forEach(prefix -> {
				boolean wasNew = uniqueDerivations.add(prefix);
				if (wasNew) {
					derivationsPrefixes.add(prefix);
				}
			});
		}
		return derivationsPrefixes;
	}

	private Stream<Symbol> derivationPrefixes(Symbol symbol) {
		return grammar.rules().stream().filter(leftIsEqualTo(symbol)).map(Rule::rightPrefix);
	}
}

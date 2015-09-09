package io.github.theangrydev.opper.recogniser.prediction;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ComputedRulePrediction implements RulePrediction {

	private final Logger logger;
	private final Grammar grammar;

	public ComputedRulePrediction(Logger logger, Grammar grammar) {
		this.logger = logger;
		this.grammar = grammar;
	}

	@Override
	public List<Rule> rulesThatCanBeReachedFrom(Symbol startSymbol) {
		// prediction
		// aim is to find rules with LHS that is the prefix of any string that can be derived from the given symbol
		List<Symbol> derivationPrefixes = determineDerivationPrefixes(startSymbol);
		logger.log(() -> "Derivation prefixes are: " + derivationPrefixes);
		List<Rule> applicableRules = determineApplicableRules(derivationPrefixes);
		logger.log(() -> "Applicable rules are: " + applicableRules);
		return applicableRules;
	}

	private List<Rule> determineApplicableRules(List<Symbol> derivationPrefixes) {
		return grammar.rules().stream().filter(startsWithOneOf(derivationPrefixes)).collect(toList());
	}

	private Predicate<Rule> startsWithOneOf(List<Symbol> derivationPrefixes) {
		return rule -> derivationPrefixes.contains(rule.start());
	}

	private List<Symbol> determineDerivationPrefixes(Symbol startSymbol) {
		List<Symbol> confirmedPrefixes = new ObjectArrayList<>();
		Set<Symbol> uniquePrefixes = new ObjectArraySet<>();
		confirmedPrefixes.add(startSymbol);
		uniquePrefixes.add(startSymbol);
		for (Symbol confirmedPrefix : confirmedPrefixes) {
			rulesThatStartWith(confirmedPrefix).map(Rule::derivationPrefix).forEach(derivationPrefix -> {
				boolean wasNew = uniquePrefixes.add(derivationPrefix);
				if (wasNew) {
					confirmedPrefixes.add(derivationPrefix);
				}
			});
		}
		return confirmedPrefixes;
	}

	private Stream<Rule> rulesThatStartWith(Symbol symbol) {
		return grammar.rules().stream().filter(startsWith(symbol));
	}

	private Predicate<Rule> startsWith(Symbol symbol) {
		return rule -> symbol.equals(rule.start());
	}
}

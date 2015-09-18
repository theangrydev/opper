package io.github.theangrydev.opper.recogniser.precomputed.prediction;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.precomputed.DerivationConsequences;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class ComputedRulePrediction implements RulePrediction {

	private final DerivationConsequences derivationPrefixes;
	private final Grammar grammar;

	public ComputedRulePrediction(Grammar grammar) {
		this.grammar = grammar;
		this.derivationPrefixes = new DerivationConsequences(grammar, Rule::derivationPrefix);
	}

	@Override
	public List<Rule> rulesThatCanBeTriggeredBy(Symbol symbol) {
		return rulesTriggeredBy(derivationPrefixes.of(symbol));
	}

	private List<Rule> rulesTriggeredBy(Set<Symbol> symbols) {
		return grammar.rules().stream().filter(triggeredByOneOf(symbols)).collect(toList());
	}

	private Predicate<Rule> triggeredByOneOf(Set<Symbol> symbols) {
		return rule -> symbols.contains(rule.trigger());
	}
}

package io.github.theangrydev.opper.parser.precomputed.prediction;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.early.DottedRule;
import io.github.theangrydev.opper.parser.precomputed.DerivationConsequences;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class ComputedRulePrediction implements RulePrediction {

	private final DottedRuleFactory dottedRuleFactory;
	private final DerivationConsequences derivationPrefixes;
	private final Grammar grammar;

	public ComputedRulePrediction(Grammar grammar) {
		this.dottedRuleFactory = new DottedRuleFactory(grammar);
		this.derivationPrefixes = new DerivationConsequences(grammar, Rule::derivationPrefix);
		this.grammar = grammar;
	}

	@Override
	public List<DottedRule> rulesThatCanBeTriggeredBy(Symbol symbol) {
		return rulesTriggeredBy(derivationPrefixes.of(symbol));
	}

	@Override
	public DottedRule initial() {
		return dottedRuleFactory.begin(grammar.acceptanceRule());
	}

	private List<DottedRule> rulesTriggeredBy(Set<Symbol> symbols) {
		return grammar.rules().stream().filter(triggeredByOneOf(symbols)).map(dottedRuleFactory::begin).collect(toList());
	}

	private Predicate<Rule> triggeredByOneOf(Set<Symbol> symbols) {
		return rule -> symbols.contains(rule.trigger());
	}
}

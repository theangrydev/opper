package io.github.theangrydev.opper.parser.precomputed.nullable;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.parser.early.DottedRule;
import io.github.theangrydev.opper.parser.precomputed.prediction.RulePrediction;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DirectNullableRuleComputer implements NullableRuleComputer {

	private final Grammar grammar;
	private final NullableRuleComputer computer;
	private final RulePrediction rulePrediction;

	public DirectNullableRuleComputer(Grammar grammar, RulePrediction rulePrediction, NullableRuleComputer computer) {
		this.grammar = grammar;
		this.computer = computer;
		this.rulePrediction = rulePrediction;
	}

	@Override
	public boolean isNullable(Rule rule) {
		if (derivationIsDirectlyEmpty(rule)) {
			return true;
		}
		if (derivationContainsATerminal(rule)) {
			return false;
		}
		List<Rule> triggered = rule.derivation().stream()
			.map(rulePrediction::rulesThatCanBeTriggeredBy)
			.flatMap(Collection::stream)
			.map(DottedRule::rule)
			.collect(toList());

		return !triggered.isEmpty() && triggered.stream().allMatch(computer::isNullable);
	}

	private boolean derivationContainsATerminal(Rule rule) {
		return rule.derivation().stream().map(rulePrediction::rulesThatCanBeTriggeredBy).anyMatch(List::isEmpty);
	}

	private boolean derivationIsDirectlyEmpty(Rule rule) {
		return rule.derivation().stream().allMatch(symbol -> symbol == grammar.emptySymbol());
	}
}

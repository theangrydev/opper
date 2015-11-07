package io.github.theangrydev.opper.parser.precomputed.nullable;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.parser.precomputed.prediction.RulePrediction;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CachingNullableRuleComputer implements NullableRuleComputer {

	private final List<NullableRuleCheck> nullableRuleChecks;
	private final NullableRuleComputer computer;

	public CachingNullableRuleComputer(Grammar grammar, RulePrediction rulePrediction) {
		this.computer = new DirectNullableRuleComputer(grammar, rulePrediction, this);
		this.nullableRuleChecks = grammar.rules().stream().map(NullableRuleCheck::nullCheck).collect(toList());
	}

	@Override
	public boolean isNullable(Rule rule) {
		NullableRuleCheck nullableRuleCheck = nullableRuleChecks.get(rule.id());
		if (nullableRuleCheck.hasBeenChecked()) {
			return nullableRuleCheck.isNullable();
		}
		if (nullableRuleCheck.isChecking()) {
			return true;
		}
		nullableRuleCheck.startChecking();
		boolean isNullable = computer.isNullable(rule);
		nullableRuleCheck.markAsNullable(isNullable);
		return isNullable;
	}

	private static class NullableRuleCheck {
		private boolean isChecking;
		private boolean hasBeenChecked;
		private boolean isNullable;
		private final Rule rule;

		private NullableRuleCheck(Rule rule) {
			this.rule = rule;
		}

		public static NullableRuleCheck nullCheck(Rule rule) {
			return new NullableRuleCheck(rule);
		}

		public boolean hasBeenChecked() {
			return hasBeenChecked;
		}

		public boolean isNullable() {
			return isNullable;
		}

		public Rule rule() {
			return rule;
		}

		public void markAsNullable(boolean isNullable) {
			this.isNullable = isNullable;
			hasBeenChecked = true;
			isChecking = false;
		}

		public void startChecking() {
			isChecking = true;
		}

		public boolean isChecking() {
			return isChecking;
		}
	}
}

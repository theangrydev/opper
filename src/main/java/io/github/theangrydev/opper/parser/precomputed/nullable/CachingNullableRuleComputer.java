/**
 * Copyright 2015 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
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

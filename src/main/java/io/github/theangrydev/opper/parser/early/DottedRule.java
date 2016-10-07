/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
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
package io.github.theangrydev.opper.parser.early;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;

import java.util.Optional;

public class DottedRule {

	private final Symbol postDot;
	private final Optional<Symbol> penult;
	private final DottedRule next;
	private final Rule rule;
	private final int dotPosition;
	private final boolean isComplete;

	private DottedRule(Rule rule, int dotPosition) {
		this.rule = rule;
		this.dotPosition = dotPosition;
		this.next = computeNext(rule, dotPosition);
		this.postDot = computePostDot(rule, dotPosition);
		this.penult = computePenult(dotPosition, rule, postDot);
		this.isComplete = dotPosition == rule.derivationLength();
	}

	private static DottedRule computeNext(Rule rule, int dotPosition) {
		if (dotPosition < rule.derivationLength()) {
			return new DottedRule(rule, dotPosition + 1);
		} else {
			return null;
		}
	}

	private static Symbol computePostDot(Rule rule, int dotPosition) {
		if (dotPosition < rule.derivationLength()) {
			return rule.derivation(dotPosition);
		} else {
			return null;
		}
	}

	private static Optional<Symbol> computePenult(int dotPosition, Rule rule, Symbol postDot) {
		if (dotPosition == rule.derivationSuffixDotPosition()) {
			return Optional.of(postDot);
		} else {
			return Optional.empty();
		}
	}

	public static DottedRule begin(Rule rule) {
		return new DottedRule(rule, 0);
	}

	public Rule rule() {
		return rule;
	}

	public Symbol postDot() {
		return postDot;
	}

	public DottedRule advance() {
		return next;
	}

	public Optional<Symbol> penult() {
		return penult;
	}

	public Symbol trigger() {
		return rule.trigger();
	}

	public boolean isCompletedAcceptanceRule(Symbol acceptanceSymbol) {
		return rule.trigger() == acceptanceSymbol && isComplete();
	}

	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(rule.trigger());
		string.append(" -> ");
		for (int i = 0; i < dotPosition; i++) {
			string.append(rule.derivation(i));
			string.append(" ");
		}
		string.append(".");
		for (int i = dotPosition; i < rule.derivationLength(); i++) {
			string.append(" ");
			string.append(rule.derivation(i));
		}
		return string.toString();
	}
}

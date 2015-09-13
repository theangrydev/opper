package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;

public class DottedRule {

	private final Rule rule;
	private final int dotPosition;

	private DottedRule(Rule rule, int dotPosition) {
		this.rule = rule;
		this.dotPosition = dotPosition;
	}

	public static DottedRule begin(Rule rule) {
		return new DottedRule(rule, 0);
	}

	public Symbol postDot() {
		checkThatDotPositionIsInsideRule();
		return rule.derivation(dotPosition);
	}

	public DottedRule next() {
		checkThatDotPositionIsInsideRule();
		return new DottedRule(rule, dotPosition + 1);
	}

	public Symbol trigger() {
		return rule.trigger();
	}

	private void checkThatDotPositionIsInsideRule() {
		checkState(dotPosition < rule.derivationLength(), "The dot position is past the end of the rule (dot at %s, rule length is %s)", dotPosition, rule.derivationLength());
	}

	public boolean isCompletedAcceptanceRule(Symbol acceptanceSymbol) {
		return rule.trigger().equals(acceptanceSymbol) && isComplete();
	}

	public boolean isComplete() {
		return dotPosition == rule.derivationLength();
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
		string.append(". ");
		for (int i = dotPosition; i < rule.derivationLength(); i++) {
			string.append(rule.derivation(i));
			string.append(" ");
		}
		return string.toString();
	}

	@Override
	public int hashCode() {
		return rule.hashCode() + 31 * dotPosition;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final DottedRule other = (DottedRule) obj;
		return Objects.equals(this.rule, other.rule)
			&& Objects.equals(this.dotPosition, other.dotPosition);
	}
}

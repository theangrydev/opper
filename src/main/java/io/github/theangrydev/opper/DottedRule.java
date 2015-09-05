package io.github.theangrydev.opper;

import java.lang.*;
import java.lang.String;

import static com.google.common.base.Preconditions.checkState;

public class DottedRule {

	private final Rule rule;
	private final int dotPosition;

	public DottedRule(Rule rule, int dotPosition) {
		this.rule = rule;
		this.dotPosition = dotPosition;
	}

	public Symbol postDot() {
		checkThatDotPositionIsInsideRule();
		return rule.symbolAt(dotPosition);
	}

	public DottedRule next() {
		checkThatDotPositionIsInsideRule();
		return new DottedRule(rule, dotPosition + 1);
	}

	public Symbol left() {
		return rule.left();
	}

	private void checkThatDotPositionIsInsideRule() {
		checkState(dotPosition < rule.length(), "The dot position is past the end of the rule (dot at %s, rule length is %s)", dotPosition, rule.length());
	}

	public boolean canAccept(Symbol acceptanceSymbol) {
		return rule.left().equals(acceptanceSymbol) && isComplete();
	}

	public boolean isComplete() {
		return dotPosition == rule.length();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DottedRule that = (DottedRule) o;
		return dotPosition == that.dotPosition && !(rule != null ? !rule.equals(that.rule) : that.rule != null);

	}

	@Override
	public int hashCode() {
		int result = rule != null ? rule.hashCode() : 0;
		result = 31 * result + dotPosition;
		return result;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(rule.left());
		string.append(" -> ");
		for (int i = 0; i < dotPosition; i++) {
			string.append(rule.symbolAt(i));
			string.append(" ");
		}
		string.append(". ");
		for (int i = dotPosition; i < rule.length(); i++) {
			string.append(rule.symbolAt(i));
		}
		return string.toString();
	}
}

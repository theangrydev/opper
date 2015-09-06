package io.github.theangrydev.opper;

import java.lang.*;
import java.lang.String;

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

	public boolean isCompletedAcceptanceRule(Symbol acceptanceSymbol) {
		return rule.left().equals(acceptanceSymbol) && isComplete();
	}

	public boolean isComplete() {
		return dotPosition == rule.length();
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
			string.append(" ");
		}
		return string.toString();
	}
}

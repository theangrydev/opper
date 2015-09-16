package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;

import java.util.Optional;

public class DottedRule {

	private final DottedRule next;
	private final Rule rule;
	private final int dotPosition;

	private DottedRule(Rule rule, int dotPosition) {
		this.rule = rule;
		this.dotPosition = dotPosition;
		if (dotPosition < rule.derivationLength()) {
			this.next = new DottedRule(rule, dotPosition + 1);
		} else {
			this.next = null;
		}
	}

	public static DottedRule begin(Rule rule) {
		return new DottedRule(rule, 0);
	}

	public Rule rule() {
		return rule;
	}

	public Symbol postDot() {
		return rule.derivation(dotPosition);
	}

	public DottedRule next() {
		return next;
	}

	public Optional<Symbol> penult() {
		if (dotPosition == rule.derivationSuffixDotPosition()) {
			return Optional.of(postDot());
		} else {
			return Optional.empty();
		}
	}

	public Symbol trigger() {
		return rule.trigger();
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
}

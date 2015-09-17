package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Symbol;

public class EarlyItem implements EarlyOrLeoItem {

	private final DottedRule dottedRule;
	private final int origin;

	public EarlyItem(DottedRule dottedRule, int origin) {
		this.dottedRule = dottedRule;
		this.origin = origin;
	}

	public boolean hasCompletedAcceptanceRule(Symbol acceptanceSymbol) {
		return origin == 0 && dottedRule.isCompletedAcceptanceRule(acceptanceSymbol);
	}

	@Override
	public DottedRule transition(Symbol symbol) {
		return dottedRule.next();
	}

	@Override
	public int origin() {
		return origin;
	}

	public Symbol trigger() {
		return dottedRule.trigger();
	}

	@Override
	public DottedRule dottedRule() {
		return dottedRule;
	}

	@Override
	public String toString() {
		return dottedRule + " @ " + origin;
	}

	public boolean equals(EarlyItem other) {
		return this.origin == other.origin && this.dottedRule == other.dottedRule;
	}
}

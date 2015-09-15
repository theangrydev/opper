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

	@Override
	public int hashCode() {
		return dottedRule.hashCode() + 31 * origin;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final EarlyItem other = (EarlyItem) obj;
		return this.dottedRule.equals(other.dottedRule)
			&& this.origin == other.origin;
	}
}

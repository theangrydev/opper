package io.github.theangrydev.opper;

import com.google.common.base.Preconditions;

import java.lang.*;
import java.lang.String;
import java.util.Optional;

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
		Preconditions.checkArgument(symbol.equals(dottedRule.postDot()), "Cannot transition rule %s under symbol %s because the post dot symbol does not match", dottedRule, symbol);
		return dottedRule.next();
	}

	@Override
	public int origin() {
		return origin;
	}

	public Optional<Symbol> leftOfCompletedRule() {
		if (dottedRule.isComplete()) {
			return Optional.of(dottedRule.left());
		} else {
			return Optional.empty();
		}
	}

	public DottedRule dottedRule() {
		return dottedRule;
	}

	@Override
	public String toString() {
		return dottedRule + " @ " + origin;
	}
}

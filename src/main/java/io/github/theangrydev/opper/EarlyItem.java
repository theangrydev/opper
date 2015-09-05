package io.github.theangrydev.opper;

import com.google.common.base.Preconditions;

import java.lang.*;
import java.lang.String;
import java.util.Optional;

public class EarlyItem implements EarlyOrLeoItem, Comparable<EarlyItem> {

	private static int ID_SEQUENCE = 0;

	private final DottedRule dottedRule;
	private final int origin;
	private final int id;

	public EarlyItem(DottedRule dottedRule, int origin) {
		this.dottedRule = dottedRule;
		this.origin = origin;
		this.id = ID_SEQUENCE++;
	}

	public boolean canAccept(Symbol acceptanceSymbol) {
		return origin == 0 && dottedRule.canAccept(acceptanceSymbol);
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

	public Optional<Symbol> leftOfCompletedRules() {
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

	@Override
	public int compareTo(EarlyItem earlyItem) {
		return Integer.compare(id, earlyItem.id);
	}
}

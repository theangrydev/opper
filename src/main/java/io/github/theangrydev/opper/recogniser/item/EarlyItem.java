package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySet;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public abstract class EarlyItem {
	protected final TransitionsEarlySetsBySymbol transitions;
	protected final DottedRule dottedRule;

	protected EarlyItem(TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
		this.transitions = transitions;
		this.dottedRule = dottedRule;
	}

	public DottedRule dottedRule() {
		return dottedRule;
	}

	public TransitionsEarlySetsBySymbol transitions() {
		return transitions;
	}

	public TransitionsEarlySet reductionTransitions() {
		return transitions.forSymbol(dottedRule.trigger());
	}

	public boolean hasCompletedAcceptanceRule(TransitionsEarlySetsBySymbol initialTransitions, Symbol acceptanceSymbol) {
		return transitions == initialTransitions && dottedRule.isCompletedAcceptanceRule(acceptanceSymbol);
	}

	public boolean isComplete() {
		return dottedRule.isComplete();
	}

	public Symbol postDot() {
		return dottedRule.postDot();
	}

	public abstract EarlyItem transition();

	@Override
	public boolean equals(Object object) {
		if (getClass() != object.getClass()) {
			return false;
		}
		final EarlyItem other = (EarlyItem) object;
		return this.transitions == other.transitions && this.dottedRule == other.dottedRule;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + dottedRule.toString();
	}
}

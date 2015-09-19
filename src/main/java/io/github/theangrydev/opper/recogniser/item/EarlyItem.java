package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySet;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public class EarlyItem implements EarlyOrLeoItem {

	private final TransitionsEarlySetsBySymbol transitions;
	private final DottedRule dottedRule;

	public EarlyItem(TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
		this.transitions = transitions;
		this.dottedRule = dottedRule;
	}

	public boolean hasCompletedAcceptanceRule(TransitionsEarlySetsBySymbol initialTransitions, Symbol acceptanceSymbol) {
		return transitions == initialTransitions && dottedRule.isCompletedAcceptanceRule(acceptanceSymbol);
	}

	@Override
	public EarlyItem transition() {
		return new EarlyItem(transitions, dottedRule.next());
	}

	public TransitionsEarlySet reductionTransitions() {
		return transitions.forSymbol(dottedRule.trigger());
	}

	@Override
	public TransitionsEarlySetsBySymbol transitions() {
		return transitions;
	}

	@Override
	public DottedRule dottedRule() {
		return dottedRule;
	}

	@Override
	public String toString() {
		return dottedRule + " @ " + transitions;
	}

	public boolean equals(EarlyItem other) {
		return this.transitions == other.transitions && this.dottedRule == other.dottedRule;
	}

	public boolean isComplete() {
		return dottedRule.isComplete();
	}

	public Symbol postDot() {
		return dottedRule.postDot();
	}
}

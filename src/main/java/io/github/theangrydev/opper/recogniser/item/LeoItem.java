package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public class LeoItem implements EarlyOrLeoItem {

	private final TransitionsEarlySetsBySymbol transitions;
	private final DottedRule top;

	public LeoItem(DottedRule top, TransitionsEarlySetsBySymbol transitions) {
		this.top = top;
		this.transitions = transitions;
	}

	@Override
	public DottedRule transition() {
		return top;
	}

	@Override
	public TransitionsEarlySetsBySymbol transitions() {
		return transitions;
	}

	@Override
	public String toString() {
		return top + " @ " + transitions;
	}

	@Override
	public DottedRule dottedRule() {
		return top;
	}
}

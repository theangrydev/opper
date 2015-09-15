package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Symbol;

public class LeoItem implements EarlyOrLeoItem {

	private final DottedRule top;
	private final Symbol transition;
	private final int origin;

	public LeoItem(DottedRule top, Symbol transition, int origin) {
		this.top = top;
		this.transition = transition;
		this.origin = origin;
	}

	@Override
	public DottedRule transition(Symbol symbol) {
		return top;
	}

	@Override
	public int origin() {
		return origin;
	}

	@Override
	public String toString() {
		return top + " via " + transition + " @ " + origin;
	}
}

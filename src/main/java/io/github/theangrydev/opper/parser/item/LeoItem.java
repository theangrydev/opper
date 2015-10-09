package io.github.theangrydev.opper.parser.item;

import io.github.theangrydev.opper.parser.transition.TransitionsEarlySetsBySymbol;

public class LeoItem extends EarlyItem {

	public LeoItem(DottedRule top, TransitionsEarlySetsBySymbol transitions) {
		super(transitions, top);
	}

	@Override
	public EarlyItem advance() {
		return this;
	}
}

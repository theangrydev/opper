package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public class LeoItem extends EarlyItem {

	public LeoItem(DottedRule top, TransitionsEarlySetsBySymbol transitions) {
		super(transitions, top);
	}

	@Override
	public EarlyItem next() {
		return this;
	}
}

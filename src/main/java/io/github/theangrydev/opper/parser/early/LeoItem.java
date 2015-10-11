package io.github.theangrydev.opper.parser.early;

public class LeoItem extends EarlyItem {

	public LeoItem(DottedRule top, TransitionsEarlySetsBySymbol transitions) {
		super(transitions, top);
	}

	@Override
	public EarlyItem advance() {
		return this;
	}
}

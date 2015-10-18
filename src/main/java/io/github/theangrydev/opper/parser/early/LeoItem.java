package io.github.theangrydev.opper.parser.early;

public class LeoItem extends EarlyItem {

	public LeoItem(DottedRule top, EarlyItem earlyItem) {
		super(earlyItem.parseTree.copy(), earlyItem.origin(), top);
	}

	@Override
	public EarlyItem advance() {
		return this;
	}
}

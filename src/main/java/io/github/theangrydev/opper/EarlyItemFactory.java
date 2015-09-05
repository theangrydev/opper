package io.github.theangrydev.opper;

public class EarlyItemFactory {

	private int idSequence;

	public EarlyItem createEarlyItem(DottedRule dottedRule, int origin) {
		return new EarlyItem(idSequence++, dottedRule, origin);
	}
}

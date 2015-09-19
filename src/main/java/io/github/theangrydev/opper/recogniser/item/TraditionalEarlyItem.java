package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public class TraditionalEarlyItem extends EarlyItem {

	private TraditionalEarlyItem next;

	public TraditionalEarlyItem(TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
		super(transitions, dottedRule);
	}

	@Override
	public EarlyItem transition() {
		if (next == null) {
			next = new TraditionalEarlyItem(transitions, dottedRule.next());
		}
		return next;
	}

	@Override
	public TransitionsEarlySetsBySymbol transitions() {
		return transitions;
	}

	@Override
	public DottedRule dottedRule() {
		return dottedRule;
	}
}

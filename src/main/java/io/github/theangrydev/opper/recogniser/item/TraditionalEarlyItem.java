package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public class TraditionalEarlyItem extends EarlyItem {

	public TraditionalEarlyItem(TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
		super(transitions, dottedRule);
	}

	@Override
	public EarlyItem transition() {
		return new TraditionalEarlyItem(transitions, dottedRule.next());
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
		return dottedRule.toString();
	}
}

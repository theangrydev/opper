package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public class TraditionalEarlyItem extends EarlyItem {

	public TraditionalEarlyItem(TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
		super(transitions, dottedRule);
	}

	public TraditionalEarlyItem(ParseTree parseTree, TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
		super(parseTree, transitions, dottedRule);
	}

	@Override
	public EarlyItem advance() {
		return new TraditionalEarlyItem(parseTree.copy(), origin, dottedRule.advance());
	}
}

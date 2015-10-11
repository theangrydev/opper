package io.github.theangrydev.opper.parser.early;

import io.github.theangrydev.opper.parser.tree.ParseTreeNode;

public class TraditionalEarlyItem extends EarlyItem {

	public TraditionalEarlyItem(TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
		super(transitions, dottedRule);
	}

	private TraditionalEarlyItem(ParseTreeNode parseTree, TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
		super(parseTree, transitions, dottedRule);
	}

	@Override
	public EarlyItem advance() {
		return new TraditionalEarlyItem(parseTree.copy(), origin, dottedRule.advance());
	}
}

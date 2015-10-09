package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Rule;

public abstract class ParseTree {
	private final Rule rule;

	public ParseTree(Rule rule) {
		this.rule = rule;
	}

	public Rule rule() {
		return rule;
	}

	public interface Visitor<T> {
		T visit(ParseTreeLeaf parseTreeLeaf);
		T visit(ParseTreeNode parseTreeNode);
	}

	public abstract <T> T visit(Visitor<T> visitor);
}

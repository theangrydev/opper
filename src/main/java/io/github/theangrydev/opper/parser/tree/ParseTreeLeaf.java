package io.github.theangrydev.opper.parser.tree;

import io.github.theangrydev.opper.grammar.Rule;

public class ParseTreeLeaf extends ParseTree {
	private final String content;

	private ParseTreeLeaf(Rule rule, String content) {
		super(rule);
		this.content = content;
	}

	public static ParseTreeLeaf leaf(Rule rule, String content) {
		return new ParseTreeLeaf(rule, content);
	}

	public String content() {
		return content;
	}

	@Override
	public String toString() {
		return content;
	}

	@Override
	public <T> T visit(Visitor<T> visitor) {
		return visitor.visit(this);
	}
}

package io.github.theangrydev.opper.parser.tree;

import io.github.theangrydev.opper.grammar.Rule;

import java.util.Collections;
import java.util.List;

public class ParseTreeLeaf extends ParseTree {
	private final String content;

	private ParseTreeLeaf(Rule rule, String content) {
		super(rule);
		this.content = content;
	}

	public static ParseTreeLeaf leaf(Rule rule, String content) {
		return new ParseTreeLeaf(rule, content);
	}

	@Override
	public String content() {
		return content;
	}

	@Override
	public List<ParseTree> children() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return content;
	}
}

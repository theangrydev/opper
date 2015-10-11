package io.github.theangrydev.opper.parser.tree;

import io.github.theangrydev.opper.grammar.Rule;

import java.util.List;

public abstract class ParseTree {
	private final Rule rule;

	public ParseTree(Rule rule) {
		this.rule = rule;
	}

	public Rule rule() {
		return rule;
	}

	public abstract String content();
	public abstract List<ParseTree> children();
}

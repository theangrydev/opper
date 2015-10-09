package io.github.theangrydev.opper.recogniser;

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

	public abstract List<ParseTree> children();
	public abstract String content();
}

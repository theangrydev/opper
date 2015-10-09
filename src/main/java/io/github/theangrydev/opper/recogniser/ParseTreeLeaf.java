package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Rule;

import java.util.Collections;
import java.util.List;

public class ParseTreeLeaf extends ParseTree {
	private final String content;

	public ParseTreeLeaf(Rule rule, String content) {
		super(rule);
		this.content = content;
	}

	@Override
	public List<ParseTree> children() {
		return Collections.emptyList();
	}

	@Override
	public String content() {
		return content;
	}

	@Override
	public String toString() {
		return content;
	}
}

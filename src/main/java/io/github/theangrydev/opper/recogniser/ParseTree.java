package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Rule;

import java.util.ArrayList;
import java.util.List;

public class ParseTree {

	private final Rule rule;
	private List<ParseTree> children;
	private String content;

	public ParseTree(Rule rule) {
		this(rule, new ArrayList<>(), "");
	}

	private ParseTree(Rule rule, List<ParseTree> children, String content) {
		this.rule = rule;
		this.children = children;
		this.content = content;
	}

	public ParseTree copy() {
		return new ParseTree(rule, new ArrayList<>(children), content);
	}

	public void withContent(String content) {
		this.content = content;
	}

	public void withChild(ParseTree child) {
		children.add(child);
	}

	public Rule rule() {
		return rule;
	}

	public List<ParseTree> children() {
		return children;
	}

	public String content() {
		return content;
	}

	@Override
	public String toString() {
		if (content.isEmpty()) {
			return children.toString();
		} else {
			return content + " " + children;
		}
	}
}

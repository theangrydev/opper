package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Rule;

import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode extends ParseTree {
	private List<ParseTree> children;

	public ParseTreeNode(Rule rule) {
		this(rule, new ArrayList<>(rule.derivationLength()));
	}

	private ParseTreeNode(Rule rule, List<ParseTree> children) {
		super(rule);
		this.children = children;
	}

	public ParseTreeNode copy() {
		return new ParseTreeNode(rule(), new ArrayList<>(children));
	}

	public void withContent(String content) {
		children.add(new ParseTreeLeaf(rule(), content));
	}

	public void withChild(ParseTree child) {
		children.add(child);
	}

	@Override
	public List<ParseTree> children() {
		return children;
	}

	@Override
	public String content() {
		return "";
	}

	@Override
	public String toString() {
		return children.toString();
	}
}

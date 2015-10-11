package io.github.theangrydev.opper.parser.tree;

import io.github.theangrydev.opper.grammar.Rule;

import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.parser.tree.ParseTreeLeaf.leaf;

public class ParseTreeNode extends ParseTree {
	private List<ParseTree> children;

	private ParseTreeNode(Rule rule, List<ParseTree> children) {
		super(rule);
		this.children = children;
	}

	public static ParseTreeNode node(Rule rule) {
		return new ParseTreeNode(rule, new ArrayList<>(rule.derivationLength()));
	}

	public ParseTreeNode copy() {
		return new ParseTreeNode(rule(), new ArrayList<>(children));
	}

	public void withContent(String content) {
		children.add(leaf(rule(), content));
	}

	public void withChild(ParseTree child) {
		children.add(child);
	}

	public List<ParseTree> children() {
		return children;
	}

	@Override
	public String toString() {
		return children.toString();
	}

	@Override
	public <T> T visit(Visitor<T> visitor) {
		return visitor.visit(this);
	}
}

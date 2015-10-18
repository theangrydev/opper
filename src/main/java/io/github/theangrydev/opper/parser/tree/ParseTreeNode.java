package io.github.theangrydev.opper.parser.tree;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.scanner.Location;

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

	public void withContent(String content, Location location) {
		children.add(leaf(rule(), content, location));
	}

	public void withChild(ParseTree child) {
		children.add(child);
	}

	@Override
	public String content() {
		return "";
	}

	@Override
	public List<ParseTree> children() {
		return children;
	}

	@Override
	public Location location() {
		return Location.between(firstChild().location(), lastChild().location());
	}

	@Override
	public String toString() {
		return children.toString();
	}

	public ParseTree firstChild() {
		return children().get(0);
	}

	public ParseTree lastChild() {
		return children().get(children().size() - 1);
	}
}

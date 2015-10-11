package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.List;

public abstract class ParseTreeNodeAnalyser<T> implements ParseTreeAnalyser<T> {

	@Override
	public final T analyse(ParseTree parseTree) {
		return analyse(parseTree.children());
	}

	protected abstract T analyse(List<ParseTree> children);
}

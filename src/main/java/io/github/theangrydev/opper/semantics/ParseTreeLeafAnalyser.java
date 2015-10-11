package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

public abstract class ParseTreeLeafAnalyser<T> implements ParseTreeAnalyser<T> {

	@Override
	public T analyse(ParseTree parseTree) {
		return analyse(parseTree.content());
	}

	protected abstract T analyse(String content);
}

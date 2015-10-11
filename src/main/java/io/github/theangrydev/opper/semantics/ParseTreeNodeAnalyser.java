package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.List;

public class ParseTreeNodeAnalyser<T> implements ParseTreeAnalyser<T> {

	private final NodeAnalyser<T> nodeAnalyser;

	private ParseTreeNodeAnalyser(NodeAnalyser<T> nodeAnalyser) {
		this.nodeAnalyser = nodeAnalyser;
	}

	public static <T> ParseTreeAnalyser<T> analyser(NodeAnalyser<T> nodeAnalyser) {
		return new ParseTreeNodeAnalyser<>(nodeAnalyser);
	}

	@Override
	public final T analyse(ParseTree parseTree) {
		return nodeAnalyser.analyse(parseTree.children());
	}

	@FunctionalInterface
	interface NodeAnalyser<T> {
		T analyse(List<ParseTree> children);
	}
}

package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.List;

public class ParseTreeNodeAnalyser<Result> implements ParseTreeAnalyser<Result> {

	private final NodeAnalyser<Result> nodeAnalyser;

	private ParseTreeNodeAnalyser(NodeAnalyser<Result> nodeAnalyser) {
		this.nodeAnalyser = nodeAnalyser;
	}

	public static <Result> ParseTreeAnalyser<Result> analyser(NodeAnalyser<Result> nodeAnalyser) {
		return new ParseTreeNodeAnalyser<>(nodeAnalyser);
	}

	@Override
	public final Result analyse(ParseTree parseTree) {
		return nodeAnalyser.analyse(parseTree.children());
	}

	@FunctionalInterface
	interface NodeAnalyser<Result> {
		Result analyse(List<ParseTree> children);
	}
}

package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

public class ParseTreeLeafAnalyser<Result> implements ParseTreeAnalyser<Result> {

	private final LeafAnalyser<Result> leafAnalyser;

	private ParseTreeLeafAnalyser(LeafAnalyser<Result> leafAnalyser) {
		this.leafAnalyser = leafAnalyser;
	}

	public static <Result> ParseTreeLeafAnalyser<Result> analyser(LeafAnalyser<Result> leafAnalyser) {
		return new ParseTreeLeafAnalyser<>(leafAnalyser);
	}

	@Override
	public final Result analyse(ParseTree parseTree) {
		return leafAnalyser.analyse(parseTree.content());
	}

	@FunctionalInterface
	public interface LeafAnalyser<Result> {
		Result analyse(String content);
	}
}

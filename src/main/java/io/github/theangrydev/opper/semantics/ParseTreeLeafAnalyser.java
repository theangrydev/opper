package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

public class ParseTreeLeafAnalyser<T> implements ParseTreeAnalyser<T> {

	private final LeafAnalyser<T> leafAnalyser;

	private ParseTreeLeafAnalyser(LeafAnalyser<T> leafAnalyser) {
		this.leafAnalyser = leafAnalyser;
	}

	public static <T> ParseTreeLeafAnalyser<T> analyser(LeafAnalyser<T> leafAnalyser) {
		return new ParseTreeLeafAnalyser<>(leafAnalyser);
	}

	@Override
	public final T analyse(ParseTree parseTree) {
		return leafAnalyser.analyse(parseTree.content());
	}

	@FunctionalInterface
	interface LeafAnalyser<T> {
		T analyse(String content);
	}
}

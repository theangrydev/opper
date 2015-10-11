package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.Parser;

import java.util.Optional;

public class SemanticAnalyser<Result> {

	private final Parser parser;
	private final ParseTreeAnalyser<Result> parseTreeAnalyser;

	public SemanticAnalyser(Parser parser, ParseTreeAnalyser<Result> parseTreeAnalyser) {
		this.parser = parser;
		this.parseTreeAnalyser = parseTreeAnalyser;
	}

	public Optional<Result> analyse() {
		return parser.parse().map(parseTreeAnalyser::analyse);
	}
}

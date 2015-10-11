package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.Parser;

import java.util.Optional;

public class SemanticAnalyser<T> {

	private final Parser parser;
	private final ParseTreeAnalyser<T> parseTreeAnalyser;

	public SemanticAnalyser(Parser parser, ParseTreeAnalyser<T> parseTreeAnalyser) {
		this.parser = parser;
		this.parseTreeAnalyser = parseTreeAnalyser;
	}

	public Optional<T> analyse() {
		return parser.parse().map(parseTreeAnalyser::analyse);
	}
}

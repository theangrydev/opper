package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.Optional;

public class FixedParser implements Parser {
	private final ParseTree parseTree;

	private FixedParser(ParseTree parseTree) {
		this.parseTree = parseTree;
	}

	public static FixedParser parser(ParseTree parseTree) {
		return new FixedParser(parseTree);
	}

	@Override
	public Optional<ParseTree> parse() {
		return Optional.of(parseTree);
	}
}

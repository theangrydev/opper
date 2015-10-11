package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.Optional;

public interface Parser {
	Optional<ParseTree> parse();
}

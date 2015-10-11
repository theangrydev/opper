package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

public interface ParseTreeAnalyser<T> {
	T analyse(ParseTree parseTree);
}

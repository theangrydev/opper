package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

public interface ParseTreeAnalyser<Result> {
	Result analyse(ParseTree parseTree);
}

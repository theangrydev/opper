package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;

public class ParseTreeAnalysers<Result> implements ParseTreeAnalyser<Result> {

	private Map<Rule, ParseTreeAnalyser<? extends Result>> parseTreeAnalysers;

	public ParseTreeAnalysers() {
		this.parseTreeAnalysers = new HashMap<>();
	}

	public void add(Rule rule, ParseTreeAnalyser<? extends Result> parseTreeAnalyser) {
		parseTreeAnalysers.put(rule, parseTreeAnalyser);
	}

	@Override
	public Result analyse(ParseTree parseTree) {
		return parseTreeAnalysers.get(parseTree.rule()).analyse(parseTree);
	}
}

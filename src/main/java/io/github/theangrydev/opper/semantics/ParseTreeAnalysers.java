package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;

public class ParseTreeAnalysers<T> implements ParseTreeAnalyser<T> {

	private Map<Rule, ParseTreeAnalyser<? extends T>> parseTreeAnalysers;

	public ParseTreeAnalysers() {
		this.parseTreeAnalysers = new HashMap<>();
	}

	public void add(Rule rule, ParseTreeAnalyser<? extends T> parseTreeAnalyser) {
		parseTreeAnalysers.put(rule, parseTreeAnalyser);
	}

	@Override
	public T analyse(ParseTree parseTree) {
		return parseTreeAnalysers.get(parseTree.rule()).analyse(parseTree);
	}
}

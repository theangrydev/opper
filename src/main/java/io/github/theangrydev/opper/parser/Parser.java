package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.recogniser.ParseTree;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class Parser {
	private final Map<Rule, RuleParser> ruleParsers;

	public Parser(Map<Rule, RuleParser> ruleParsers) {
		this.ruleParsers = ruleParsers;
	}

	public Object parse(ParseTree parseTree) {
		RuleParser ruleParser = ruleParsers.get(parseTree.rule());
		List<Object> arguments = parseTree.children().stream().map(this::parse).collect(toList());
		return ruleParser.parse(parseTree.content(), arguments);
	}
}

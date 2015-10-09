package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.recogniser.ParseTree;
import io.github.theangrydev.opper.recogniser.ParseTreeLeaf;
import io.github.theangrydev.opper.recogniser.ParseTreeNode;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class Parser {
	private final Map<Rule, RuleParser> ruleParsers;

	public Parser(Map<Rule, RuleParser> ruleParsers) {
		this.ruleParsers = ruleParsers;
	}

	public Object parse(ParseTree parseTree) {
		RuleParser ruleParser = ruleParsers.get(parseTree.rule());
		return ruleParser.parse(parseTree.visit(new ParseTree.Visitor<List<Object>>() {
			@Override
			public List<Object> visit(ParseTreeLeaf parseTreeLeaf) {
				return singletonList(parseTreeLeaf.content());
			}

			@Override
			public List<Object> visit(ParseTreeNode parseTreeNode) {
				return parseTreeNode.children().stream().map(Parser.this::parse).collect(toList());
			}
		}));
	}
}

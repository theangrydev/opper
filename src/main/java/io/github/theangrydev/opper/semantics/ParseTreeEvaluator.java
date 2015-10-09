package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.parser.ParseTree;
import io.github.theangrydev.opper.parser.ParseTreeLeaf;
import io.github.theangrydev.opper.parser.ParseTreeNode;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class ParseTreeEvaluator {
	private final Map<Rule, RuleEvaluator> ruleParsers;

	public ParseTreeEvaluator(Map<Rule, RuleEvaluator> ruleParsers) {
		this.ruleParsers = ruleParsers;
	}

	public Object evaluate(ParseTree parseTree) {
		RuleEvaluator ruleEvaluator = ruleParsers.get(parseTree.rule());
		return ruleEvaluator.parse(parseTree.visit(new ParseTree.Visitor<List<Object>>() {
			@Override
			public List<Object> visit(ParseTreeLeaf parseTreeLeaf) {
				return singletonList(parseTreeLeaf.content());
			}

			@Override
			public List<Object> visit(ParseTreeNode parseTreeNode) {
				return parseTreeNode.children().stream().map(ParseTreeEvaluator.this::evaluate).collect(toList());
			}
		}));
	}
}

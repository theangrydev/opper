package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.parser.Parser;
import io.github.theangrydev.opper.parser.tree.ParseTree;
import io.github.theangrydev.opper.parser.tree.ParseTreeLeaf;
import io.github.theangrydev.opper.parser.tree.ParseTreeNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class SemanticAnalyser {
	private final Map<Rule, RuleEvaluator> ruleEvaluators;
	private final Parser parser;

	public SemanticAnalyser(Map<Rule, RuleEvaluator> ruleEvaluators, Parser parser) {
		this.ruleEvaluators = ruleEvaluators;
		this.parser = parser;
	}

	public Optional<Object> analyse() {
		return parser.parse().map(this::analyse);
	}

	private Object analyse(ParseTree parseTree) {
		RuleEvaluator ruleEvaluator = ruleEvaluators.get(parseTree.rule());
		return ruleEvaluator.evaluate(parseTree.visit(new ParseTree.Visitor<List<Object>>() {
			@Override
			public List<Object> visit(ParseTreeLeaf parseTreeLeaf) {
				return singletonList(parseTreeLeaf.content());
			}

			@Override
			public List<Object> visit(ParseTreeNode parseTreeNode) {
				return parseTreeNode.children().stream().map(SemanticAnalyser.this::analyse).collect(toList());
			}
		}));
	}
}

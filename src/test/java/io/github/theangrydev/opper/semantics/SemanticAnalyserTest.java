package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.parser.ParseTree;
import io.github.theangrydev.opper.parser.ParseTreeNode;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.theangrydev.opper.parser.ParseTreeLeaf.leaf;
import static io.github.theangrydev.opper.parser.ParseTreeNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SemanticAnalyserTest {

	@Test
	public void shouldEvaluateAParseTree() {
		Rule add = rule();
		Rule times = rule();
		Rule number = rule();

		Map<Rule, RuleEvaluator> ruleEvaluators = new HashMap<>();
		ruleEvaluators.put(add, arguments -> new Addition((Numeric) arguments.get(0), (Numeric) arguments.get(1)));
		ruleEvaluators.put(times, arguments -> new Multiplication((Numeric) arguments.get(0), (Numeric) arguments.get(1)));
		ruleEvaluators.put(number, arguments -> new Number(Integer.parseInt((String) arguments.get(0))));

		SemanticAnalyser semanticAnalyser = new SemanticAnalyser(ruleEvaluators);

		ParseTree parseTree = parseTree(add, parseTree(add, parseTree(add, parseTree(number, "2"), parseTree(number, "3")), parseTree(number, "2")), parseTree(times, parseTree(number, "3"), parseTree(number, "4")));

		Object parse = semanticAnalyser.analyse(parseTree);

		assertThat(parse).hasToString("Addition{left=Addition{left=Addition{left=Number{value=2}, right=Number{value=3}}, right=Number{value=2}}, right=Multiplication{left=Number{value=3}, right=Number{value=4}}}");
	}

	private ParseTree parseTree(Rule rule, String content) {
		return leaf(rule, content);
	}

	private ParseTree parseTree(Rule rule, ParseTree... children) {
		ParseTreeNode parseTree = node(rule);
		for (ParseTree child : children) {
			parseTree.withChild(child);
		}
		return parseTree;
	}

	private class Number implements Numeric {
		private final int value;

		public Number(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		@Override
		public String toString() {
			return "Number{" +
				"value=" + value +
				'}';
		}
	}

	private class Addition implements Numeric {
		private final Numeric left;
		private final Numeric right;

		public Addition(Numeric left, Numeric right) {
			this.left = left;
			this.right = right;
		}

		public Numeric left() {
			return left;
		}
		public Numeric right() {
			return right;
		}

		@Override
		public String toString() {
			return "Addition{" +
				"left=" + left +
				", right=" + right +
				'}';
		}
	}
	private class Multiplication implements Numeric {
		private final Numeric left;
		private final Numeric right;

		public Multiplication(Numeric left, Numeric right) {
			this.left = left;
			this.right = right;
		}

		public Numeric left() {
			return left;
		}
		public Numeric right() {
			return right;
		}

		@Override
		public String toString() {
			return "Multiplication{" +
				"left=" + left +
				", right=" + right +
				'}';
		}
	}

	private interface Numeric {

	}

	private Rule rule() {
		return mock(Rule.class);
	}
}

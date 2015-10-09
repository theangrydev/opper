package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.recogniser.ParseTree;
import io.github.theangrydev.opper.recogniser.ParseTreeLeaf;
import io.github.theangrydev.opper.recogniser.ParseTreeNode;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ParserTest {

	@Test
	public void shouldParseAParseTree() {
		Rule add = rule();
		Rule times = rule();
		Rule number = rule();

		Map<Rule, RuleParser> ruleParsers = new HashMap<>();
		ruleParsers.put(add, new AddParser());
		ruleParsers.put(times, new MultiplicationParser());
		ruleParsers.put(number, new NumberParser());

		Parser parser = new Parser(ruleParsers);

		ParseTree parseTree = parseTree(add, parseTree(add, parseTree(add, parseTree(number, "2"), parseTree(number, "3")), parseTree(number, "2")), parseTree(times, parseTree(number, "3"), parseTree(number, "4")));

		Object parse = parser.parse(parseTree);

		assertThat(parse).hasToString("Addition{left=Addition{left=Addition{left=Number{value=2}, right=Number{value=3}}, right=Number{value=2}}, right=Multiplication{left=Number{value=3}, right=Number{value=4}}}");
	}

	private ParseTree parseTree(Rule rule, String content) {
		return new ParseTreeLeaf(rule, content);
	}

	private ParseTree parseTree(Rule rule, ParseTree... children) {
		ParseTreeNode parseTree = new ParseTreeNode(rule);
		for (ParseTree child : children) {
			parseTree.withChild(child);
		}
		return parseTree;
	}

	private class NumberParser implements RuleParser {
		@Override
		public Object parse(String content, List<Object> arguments) {
			return new Number(Integer.parseInt(content));
		}
	}

	private class AddParser implements RuleParser {
		@Override
		public Object parse(String content, List<Object> arguments) {
			return new Addition((Numeric) arguments.get(0), (Numeric) arguments.get(1));
		}

	}

	private class MultiplicationParser implements RuleParser {
		@Override
		public Object parse(String content, List<Object> arguments) {
			return new Multiplication((Numeric) arguments.get(0), (Numeric) arguments.get(1));
		}
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

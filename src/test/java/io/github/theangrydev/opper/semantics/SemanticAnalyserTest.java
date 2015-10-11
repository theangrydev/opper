package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.parser.Parser;
import io.github.theangrydev.opper.parser.tree.ParseTree;
import io.github.theangrydev.opper.parser.tree.ParseTreeNode;
import org.junit.Test;

import java.util.List;

import static io.github.theangrydev.opper.parser.FixedParser.parser;
import static io.github.theangrydev.opper.parser.tree.ParseTreeLeaf.leaf;
import static io.github.theangrydev.opper.parser.tree.ParseTreeNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SemanticAnalyserTest {

	@Test
	public void shouldEvaluateAParseTree() {
		Rule add = rule();
		Rule times = rule();
		Rule number = rule();

		ParseTreeAnalysers<Numeric> numericAnalysers = new ParseTreeAnalysers<>();
		numericAnalysers.addAnalyser(times, new MultiplicationAnalyser(numericAnalysers));
		numericAnalysers.addAnalyser(add, new AdditionAnalyser(numericAnalysers));
		numericAnalysers.addAnalyser(number, new NumberAnalyser());

		Parser parser = parser(parseTree(add, parseTree(add, parseTree(add, parseTree(number, "2"), parseTree(number, "3")), parseTree(number, "2")), parseTree(times, parseTree(number, "3"), parseTree(number, "4"))));

		Numeric parsed = numericAnalysers.analyse(parser.parse().get());

		assertThat(parsed).hasToString("Addition{left=Addition{left=Addition{left=Number{value=2}, right=Number{value=3}}, right=Number{value=2}}, right=Multiplication{left=Number{value=3}, right=Number{value=4}}}");
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

	private static class NumberAnalyser extends ParseTreeLeafAnalyser<Number> {

		@Override
		protected Number analyse(String content) {
			return new Number(Integer.parseInt(content));
		}
	}

	private static class Number implements Numeric {
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

	private static class AdditionAnalyser extends ParseTreeNodeAnalyser<Addition> {

		private final ParseTreeAnalysers<Numeric> numericAnalysers;

		private AdditionAnalyser(ParseTreeAnalysers<Numeric> numericAnalysers) {
			this.numericAnalysers = numericAnalysers;
		}

		@Override
		protected Addition analyse(List<ParseTree> children) {
			Numeric left = numericAnalysers.analyse(children.get(0));
			Numeric right = numericAnalysers.analyse(children.get(1));
			return new Addition(left, right);
		}
	}

	private static class Addition implements Numeric {
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

	private static class MultiplicationAnalyser extends ParseTreeNodeAnalyser<Multiplication> {

		private final ParseTreeAnalysers<Numeric> numericAnalysers;

		private MultiplicationAnalyser(ParseTreeAnalysers<Numeric> numericAnalysers) {
			this.numericAnalysers = numericAnalysers;
		}

		@Override
		protected Multiplication analyse(List<ParseTree> children) {
			Numeric left = numericAnalysers.analyse(children.get(0));
			Numeric right = numericAnalysers.analyse(children.get(1));
			return new Multiplication(left, right);
		}
	}

	private static class Multiplication implements Numeric {
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

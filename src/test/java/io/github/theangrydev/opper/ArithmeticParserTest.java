package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * <pre>
 * <P> ::= <S> # the start rule
 * <S> ::= <S> "+" <M> | <M>
 * <M> ::= <M> "*" <T> | <T>
 * <T> ::= "1" | "2" | "3" | "4"
 * </pre>
 * @see <a href="https://en.wikipedia.org/w/index.php?title=Earley_parser&oldid=667926718#Example">Early parser example</a>
 */
public class ArithmeticParserTest {

	private final ExampleGrammar grammar = new ExampleGrammar();

	@Test
	public void shouldParseALeftRecursiveGrammar() {
		Corpus corpus = new FixedCorpus(grammar.two, grammar.plus, grammar.three, grammar.times, grammar.four);

		Parser parser = new Parser(grammar, corpus);

		assertThat(parser.parse()).isTrue();
	}

	private static class ExampleGrammar implements Grammar {

		private final SymbolFactory symbolFactory = new SymbolFactory();
		private final RuleFactory ruleFactory = new RuleFactory();

		private final Symbol accept = symbolFactory.createSymbol("P");
		private final Symbol plus = symbolFactory.createSymbol("+");
		private final Symbol addition = symbolFactory.createSymbol("S");
		private final Symbol times = symbolFactory.createSymbol("*");
		private final Symbol multiplication = symbolFactory.createSymbol("M");
		private final Symbol number = symbolFactory.createSymbol("T");
		private final Symbol one = symbolFactory.createSymbol("1");
		private final Symbol two = symbolFactory.createSymbol("2");
		private final Symbol three = symbolFactory.createSymbol("3");
		private final Symbol four = symbolFactory.createSymbol("4");

		private final Rule acceptRule = ruleFactory.createRule(accept, addition);
		private final Rule addRuleRecursive = ruleFactory.createRule(addition, addition, plus, multiplication);
		private final Rule addRuleTerminal = ruleFactory.createRule(addition, multiplication);
		private final Rule multRuleRecursive = ruleFactory.createRule(multiplication, multiplication, times, number);
		private final Rule multRuleTerminal = ruleFactory.createRule(multiplication, number);
		private final Rule oneIsANumber = ruleFactory.createRule(number, one);
		private final Rule twoIsANumber = ruleFactory.createRule(number, two);
		private final Rule threeIsANumber = ruleFactory.createRule(number, three);
		private final Rule fourIsANumber = ruleFactory.createRule(number, four);

		@Override
		public List<Symbol> symbols() {
			return Arrays.asList(accept, plus, addition, times, multiplication, number, one, two, three, four);
		}

		@Override
		public List<Rule> rules() {
			return Arrays.asList(acceptRule, addRuleRecursive, addRuleTerminal, multRuleRecursive, multRuleTerminal, oneIsANumber, twoIsANumber, threeIsANumber, fourIsANumber);
		}

		@Override
		public Symbol acceptanceSymbol() {
			return accept;
		}

		@Override
		public Rule startRule() {
			return acceptRule;
		}
	}
}

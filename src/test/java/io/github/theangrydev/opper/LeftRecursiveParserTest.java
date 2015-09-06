package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class LeftRecursiveParserTest {

	private final ExampleGrammar grammar = new ExampleGrammar();

	@Test
	public void shouldParseALeftRecursiveGrammar() {
		Corpus corpus = new FixedCorpus(grammar.repeated, grammar.repeated, grammar.repeated);

		Parser parser = new Parser(grammar, corpus);

		assertThat(parser.parse()).isTrue();
	}

	private static class ExampleGrammar implements Grammar {

		private final SymbolFactory symbolFactory = new SymbolFactory();
		private final RuleFactory ruleFactory = new RuleFactory();

		private final Symbol accept = symbolFactory.createSymbol("ACCEPT");
		private final Symbol start = symbolFactory.createSymbol("START");
		private final Symbol repeated = symbolFactory.createSymbol("REPEATED");

		private final Rule acceptRule = ruleFactory.createRule(accept, start);
		private final Rule ruleWithRightRecursion = ruleFactory.createRule(start, start, repeated);
		private final Rule terminalRule = ruleFactory.createRule(start, repeated);

		@Override
		public List<Symbol> symbols() {
			return Arrays.asList(start, accept, repeated);
		}

		@Override
		public List<Rule> rules() {
			return Arrays.asList(acceptRule, terminalRule, ruleWithRightRecursion);
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

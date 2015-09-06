package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class RightRecursiveParserTest {

	private final ExampleGrammar grammar = new ExampleGrammar();

	@Test
	public void shouldParseARightRecursiveGrammar() {
		Corpus corpus = new FixedCorpus(grammar.repeated, grammar.repeated, grammar.terminal);

		Parser parser = new Parser(grammar, corpus);

		assertThat(parser.parse()).isTrue();
	}

	private static class ExampleGrammar implements Grammar {

		private final SymbolFactory symbolFactory = new SymbolFactory();
		private final RuleFactory ruleFactory = new RuleFactory();

		private final Symbol start = symbolFactory.createSymbol("START");
		private final Symbol accept = symbolFactory.createSymbol("ACCEPT");
		private final Symbol repeated = symbolFactory.createSymbol("REPEATED");
		private final Symbol terminal = symbolFactory.createSymbol("TERMINAL");

		private final Rule acceptRule = ruleFactory.createRule(accept, start);
		private final Rule ruleWithRightRecursion = ruleFactory.createRule(start, repeated, start);
		private final Rule terminalRule = ruleFactory.createRule(start, terminal);

		@Override
		public List<Symbol> symbols() {
			return Arrays.asList(start, accept, repeated, terminal);
		}

		@Override
		public List<Rule> rules() {
			return Arrays.asList(acceptRule, ruleWithRightRecursion, terminalRule);
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

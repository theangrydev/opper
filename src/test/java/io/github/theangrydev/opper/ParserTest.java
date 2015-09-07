package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ParserTest {

	private final ExampleGrammar grammar = new ExampleGrammar();

	@Test
	public void shouldParseASimpleGrammar() {
		Corpus corpus = new FixedCorpus(grammar.middle, grammar.second, grammar.dummy);

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse()).isTrue();
	}

	private static class ExampleGrammar implements Grammar {

		private final SymbolFactory symbolFactory = new SymbolFactory();
		private final RuleFactory ruleFactory = new RuleFactory();

		private final Symbol start = symbolFactory.createSymbol("START");
		private final Symbol accept = symbolFactory.createSymbol("ACCEPT");
		private final Symbol first = symbolFactory.createSymbol("FIRST");
		private final Symbol second = symbolFactory.createSymbol("SECOND");
		private final Symbol dummy = symbolFactory.createSymbol("DUMMY");
		private final Symbol middle = symbolFactory.createSymbol("MIDDLE");

		private final Rule acceptRule = ruleFactory.createRule(accept, start);
		private final Rule firstToAccept = ruleFactory.createRule(start, middle, first);
		private final Rule startToFirst = ruleFactory.createRule(first, second, dummy);

		@Override
		public List<Symbol> symbols() {
			return Arrays.asList(start, accept, first, second, dummy, middle);
		}

		@Override
		public List<Rule> rules() {
			return Arrays.asList(acceptRule, firstToAccept, startToFirst);
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

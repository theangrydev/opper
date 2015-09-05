package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ParserTest {

	private final ExampleGrammar example = new ExampleGrammar();

	@Test
	public void shouldParseASimpleGrammar() {
		Parser parser = new Parser(example, new ExampleCorpus());

		assertThat(parser.parse()).isTrue();
	}

	private class ExampleCorpus implements Corpus {

		private final List<Symbol> symbols = Arrays.asList(example.second, example.dummy);

		@Override
		public int size() {
			return symbols.size();
		}

		@Override
		public Symbol symbol(int index) {
			return symbols.get(index);
		}
	}

	private static class ExampleGrammar implements Grammar {

		private final SymbolFactory symbolFactory = new SymbolFactory();
		private final RuleFactory ruleFactory = new RuleFactory();

		private final Symbol start = symbolFactory.createSymbol("START");
		private final Symbol accept = symbolFactory.createSymbol("ACCEPT");
		private final Symbol first = symbolFactory.createSymbol("FIRST");
		private final Symbol second = symbolFactory.createSymbol("SECOND");
		private final Symbol dummy = symbolFactory.createSymbol("DUMMY");

		private final Rule acceptRule = ruleFactory.createRule(accept, second, dummy);
		private final Rule firstToAccept = ruleFactory.createRule(first, accept);
		private final Rule startToFirst = ruleFactory.createRule(start, first);

		@Override
		public List<Symbol> symbols() {
			return Arrays.asList(start, accept, first, second, dummy);
		}

		@Override
		public List<Rule> rules() {
			return Arrays.asList(acceptRule, firstToAccept);
		}

		@Override
		public Symbol acceptanceSymbol() {
			return accept;
		}

		@Override
		public Rule startRule() {
			return startToFirst;
		}
	}
}

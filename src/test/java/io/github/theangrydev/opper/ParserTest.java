package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
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

		private final Iterator<Symbol> symbols = Arrays.asList(example.second, example.dummy).iterator();

		@Override
		public Symbol nextSymbol() {
			return symbols.next();
		}

		@Override
		public boolean hasNextSymbol() {
			return symbols.hasNext();
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

		private final Rule acceptRule = ruleFactory.createRule(accept, start);
		private final Rule firstToAccept = ruleFactory.createRule(start, first);
		private final Rule startToFirst = ruleFactory.createRule(first, second, dummy);

		@Override
		public List<Symbol> symbols() {
			return Arrays.asList(start, accept, first, second, dummy);
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

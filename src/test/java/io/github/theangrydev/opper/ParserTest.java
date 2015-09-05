package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ParserTest {

	private final ExampleGrammar example = new ExampleGrammar();
	private final RightRecursiveGrammar example2 = new RightRecursiveGrammar();

	@Test
	public void shouldParseASimpleGrammar() {
		Parser parser = new Parser(example, new ExampleCorpus());

		assertThat(parser.parse()).isTrue();
	}

	@Test
	public void shouldParseASimadspleGrammar() {
		Parser parser = new Parser(example2, new Example2Corpus());

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

	private class Example2Corpus implements Corpus {

		private final Iterator<Symbol> symbols = Arrays.asList(example2.repeated, example2.second, example2.dummy).iterator();

		@Override
		public Symbol nextSymbol() {
			return symbols.next();
		}

		@Override
		public boolean hasNextSymbol() {
			return symbols.hasNext();
		}
	}

	private static class RightRecursiveGrammar implements Grammar {

		private final SymbolFactory symbolFactory = new SymbolFactory();
		private final RuleFactory ruleFactory = new RuleFactory();

		private final Symbol start = symbolFactory.createSymbol("START");
		private final Symbol accept = symbolFactory.createSymbol("ACCEPT");
		private final Symbol first = symbolFactory.createSymbol("FIRST");
		private final Symbol second = symbolFactory.createSymbol("SECOND");
		private final Symbol dummy = symbolFactory.createSymbol("DUMMY");
		private final Symbol repeated = symbolFactory.createSymbol("REPEATED");

		private final Rule acceptRule = ruleFactory.createRule(accept, second, dummy);
		private final Rule firstToAccept = ruleFactory.createRule(first, repeated, accept);
		private final Rule startToFirst = ruleFactory.createRule(start, first);

		@Override
		public List<Symbol> symbols() {
			return Arrays.asList(start, accept, first, second, dummy, repeated);
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

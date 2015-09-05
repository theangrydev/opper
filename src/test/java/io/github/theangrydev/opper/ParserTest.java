package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ParserTest {

	private final Grammar example = new ExampleGrammar();

	@Test
	public void shouldParseASimpleGrammar() {
		Parser parser = new Parser(example, new DummyCorpus());

		assertThat(parser.parse()).isTrue();
	}

	private class DummyCorpus implements Corpus {

		private final List<Symbol> symbols = Arrays.asList(ExampleGrammar.SECOND, ExampleGrammar.DUMMY);

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
		private static final Symbol DUMMY = new Symbol(4, "DUMMY");
		private static final Symbol SECOND = new Symbol(3, "SECOND");
		private static final Symbol ACCEPT = new Symbol(1, "ACCEPT");
		private static final Symbol FIRST = new Symbol(2, "FIRST");
		private static final Symbol START = new Symbol(0, "START");

		private static final Rule ACCEPT_RULE = new Rule(0, ACCEPT, new String(SECOND, DUMMY));
		private static final Rule FIRST_TO_ACCEPT = new Rule(2, FIRST, new String(ACCEPT));
		private static final Rule START_TO_FIRST = new Rule(1, START, new String(FIRST));

		@Override
		public List<Symbol> symbols() {
			return Arrays.asList(START, ACCEPT, FIRST, SECOND, DUMMY);
		}

		@Override
		public List<Rule> rules() {
			return Arrays.asList(ACCEPT_RULE, FIRST_TO_ACCEPT);
		}

		@Override
		public Symbol acceptanceSymbol() {
			return ACCEPT;
		}

		@Override
		public Rule startRule() {
			return START_TO_FIRST;
		}
	}
}

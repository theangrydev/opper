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
}

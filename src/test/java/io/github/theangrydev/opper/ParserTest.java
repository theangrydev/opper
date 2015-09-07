package io.github.theangrydev.opper;

import org.junit.Test;

import static io.github.theangrydev.opper.FixedCorpus.corpus;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class ParserTest {

	@Test
	public void shouldParseASimpleGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withSymbols("FIRST", "SECOND", "DUMMY", "MIDDLE")
			.withRule("START", "MIDDLE", "FIRST")
			.withRule("FIRST", "SECOND", "DUMMY")
			.build();
		Corpus corpus = corpus(grammar, "MIDDLE", "SECOND", "DUMMY");

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse()).isTrue();
	}
}

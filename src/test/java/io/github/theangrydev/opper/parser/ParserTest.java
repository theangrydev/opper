package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class ParserTest {

	@Test
	public void shouldRecogniseASimpleGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "MIDDLE", "FIRST")
			.withRule("FIRST", "SECOND", "DUMMY")
			.build();
		Corpus corpus = corpus(grammar, "MIDDLE", "SECOND", "DUMMY");

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse()).isPresent();
	}
}

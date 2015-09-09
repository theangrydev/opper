package io.github.theangrydev.opper;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.recogniser.Recogniser;
import org.junit.Test;

import static io.github.theangrydev.opper.FixedCorpus.corpus;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class RecogniserTest {

	@Test
	public void shouldrecogniseASimpleGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withSymbols("FIRST", "SECOND", "DUMMY", "MIDDLE")
			.withRule("START", "MIDDLE", "FIRST")
			.withRule("FIRST", "SECOND", "DUMMY")
			.build();
		Corpus corpus = corpus(grammar, "MIDDLE", "SECOND", "DUMMY");

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		assertThat(recogniser.recognise()).isTrue();
	}
}

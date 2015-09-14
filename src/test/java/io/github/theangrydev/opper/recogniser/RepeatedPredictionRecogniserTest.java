package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import java.io.IOException;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class RepeatedPredictionRecogniserTest {

	@Test
	public void shouldRecogniseALeftRecursiveGrammar() throws IOException, InterruptedException {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withSymbols("A", "B", "C")
			.withRule("START", "A", "B")
			.withRule("B", "C")
			.withRule("B", "C")
			.build();
		Corpus corpus = corpus(grammar, "A", "C");

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		assertThat(recogniser.recognise()).isTrue();
	}
}

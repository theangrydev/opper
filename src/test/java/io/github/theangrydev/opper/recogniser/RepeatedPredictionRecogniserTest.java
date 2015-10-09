package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class RepeatedPredictionRecogniserTest {

	@Test
	public void shouldRecogniseALeftRecursiveGrammar() throws Exception {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "BEGIN", "B")
			.withRule("START", "BEGIN", "C")
			.withRule("B", "REPEATED")
			.withRule("C", "REPEATED")
			.withRule("REPEATED", "END")
			.build();
		Corpus corpus = corpus(grammar, "BEGIN", "END");

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		assertThat(recogniser.parse()).isPresent();
		assertThat(recogniser.finalEarlySetSize()).isEqualTo(6);
	}
}

package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.recogniser.Recogniser;
import org.junit.Test;

import java.io.IOException;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class LeftRecursiveRecogniserTest {

	@Test
	public void shouldRecogniseALeftRecursiveGrammar() throws IOException, InterruptedException {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "START", "REPEATED")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10, grammar.symbolByName("REPEATED")));

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		assertThat(recogniser.recognise()).isTrue();
	}
}

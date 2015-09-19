package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

public class DirectlyRightRecursiveRecogniserTest {

	@Test
	public void shouldRecogniseADirectlyRightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "START")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10, grammar.symbolByName("REPEATED")));

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		assertThat(recogniser.recognise()).isTrue();
	}
}
package io.github.theangrydev.opper;

import org.junit.Test;

import java.io.IOException;

import static io.github.theangrydev.opper.FixedCorpus.corpus;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class RepeatedPredictionParserTest {

	@Test
	public void shouldParseALeftRecursiveGrammar() throws IOException, InterruptedException {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withSymbols("A", "B", "C")
			.withRule("START", "A", "B")
			.withRule("B", "C")
			.withRule("B", "C")
			.build();
		Corpus corpus = corpus(grammar, "A", "C");

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse()).isTrue();
	}
}

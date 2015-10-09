package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.scanner.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class RepeatedPredictionParserTest {

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

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse()).isPresent();
		assertThat(parser.finalEarlySetSize()).isEqualTo(6);
	}
}

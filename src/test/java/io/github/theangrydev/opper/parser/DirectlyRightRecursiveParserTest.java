package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

public class DirectlyRightRecursiveParserTest {

	@Test
	public void shouldRecogniseADirectlyRightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "START")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10, scannedSymbol(grammar.symbolByName("REPEATED"), "")));

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse()).isPresent();
	}
}

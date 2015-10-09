package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import java.io.IOException;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static io.github.theangrydev.opper.scanner.SymbolInstance.symbolInstance;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class LeftRecursiveParserTest {

	@Test
	public void shouldRecogniseALeftRecursiveGrammar() throws IOException, InterruptedException {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "START", "REPEATED")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10, symbolInstance(grammar.symbolByName("REPEATED"), "")));

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse()).isPresent();
	}
}
package io.github.theangrydev.opper;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.github.theangrydev.opper.FixedCorpus.corpus;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class LeftRecursiveParserTest {

	@Test
	public void shouldParseALeftRecursiveGrammar() throws IOException, InterruptedException {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withSymbol("REPEATED")
			.withRule("START", "START", "REPEATED")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10, grammar.symbolByName("REPEATED")));

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		Stopwatch stopwatch = Stopwatch.createStarted();
		assertThat(parser.parse()).isTrue();
		System.out.println("took " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}

	private Symbol[] repeat(Symbol symbol, int n) {
		Symbol[] symbols = new Symbol[n];
		for (int i = 0; i < n; i++) {
			symbols[i] = symbol;
		}
		return symbols;
	}
}

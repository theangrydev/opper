package io.github.theangrydev.opper;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static io.github.theangrydev.opper.FixedCorpus.corpus;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

public class RightRecursiveParserTest {

	@Test
	public void shouldParseARightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withSymbol("REPEATED")
			.withRule("START", "REPEATED", "START")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10, grammar.symbolByName("REPEATED")));

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		Stopwatch stopwatch = Stopwatch.createStarted();
		assertThat(parser.parse()).isTrue();
		System.out.println("took " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}
}

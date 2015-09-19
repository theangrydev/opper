package io.github.theangrydev.opper.recogniser;

import com.google.common.base.Stopwatch;
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static java.util.Collections.nCopies;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class UnmarkedMiddleRecursionRecogniserTest {

	@Test
	public void shouldRecogniseAGrammarWithAnUnmarkedMiddleRecursion() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "START", "REPEATED")
			.withRule("START", "REPEATED", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10, grammar.symbolByName("REPEATED")));

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		Stopwatch stopwatch = Stopwatch.createStarted();
		recogniser.recognise();
		assertThat(stopwatch.elapsed(MILLISECONDS)).describedAs("Time taken should be less than 100ms").isLessThan(100);
	}
}
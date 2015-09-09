package io.github.theangrydev.opper;

import com.google.common.base.Stopwatch;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.recogniser.Recogniser;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static io.github.theangrydev.opper.FixedCorpus.corpus;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

public class RightRecursiveRecogniserTest {

	@Test
	public void shouldrecogniseARightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withSymbol("REPEATED")
			.withRule("START", "REPEATED", "START")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10, grammar.symbolByName("REPEATED")));

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		Stopwatch stopwatch = Stopwatch.createStarted();
		assertThat(recogniser.recognise()).isTrue();
		System.out.println("took " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}
}

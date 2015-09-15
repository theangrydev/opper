package io.github.theangrydev.opper.recogniser;

import com.google.common.base.Stopwatch;
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static org.assertj.core.api.Assertions.assertThat;

public class IndireclytRightRecursiveRecogniserTest {

	@Test
	public void shouldRecogniseAnIndirectlyRightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "INDIRECT")
			.withRule("START", "REPEATED")
			.withRule("INDIRECT", "MIDDLE", "START")
			.build();
		Corpus corpus = corpus(grammar, "REPEATED", "MIDDLE", "REPEATED", "MIDDLE", "REPEATED");

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		Stopwatch stopwatch = Stopwatch.createStarted();
		assertThat(recogniser.recognise()).isTrue();
		System.out.println("took " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}
}

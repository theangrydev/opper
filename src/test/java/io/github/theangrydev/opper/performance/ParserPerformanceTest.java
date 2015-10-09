package io.github.theangrydev.opper.performance;

import com.google.common.base.Stopwatch;
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.parser.Parser;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;
import static java.util.Collections.nCopies;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Category(PerformanceTests.class)
public class ParserPerformanceTest {

	@Test
	public void shouldRecogniseADirectlyRightRecursiveGrammarInGoodTime() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "START")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10000, scannedSymbol(grammar.symbolByName("REPEATED"), "")));

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		Stopwatch stopwatch = Stopwatch.createStarted();
		parser.parse();
		assertThat(stopwatch.elapsed(MILLISECONDS)).describedAs("Time taken should be less than 100ms").isLessThan(100);
	}

	@Test
	public void shouldRecogniseALeftRecursiveGrammarInGoodTime() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "START", "REPEATED")
			.withRule("START", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(10000, scannedSymbol(grammar.symbolByName("REPEATED"), "")));

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		Stopwatch stopwatch = Stopwatch.createStarted();
		parser.parse();
		assertThat(stopwatch.elapsed(MILLISECONDS)).describedAs("Time taken should be less than 100ms").isLessThan(100);
	}

	@Test
	public void shouldRecogniseAGrammarWithAnUnmarkedMiddleRecursionInGoodTime() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "START", "REPEATED")
			.withRule("START", "REPEATED", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(100, scannedSymbol(grammar.symbolByName("REPEATED"), "")));

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		Stopwatch stopwatch = Stopwatch.createStarted();
		parser.parse();
		assertThat(stopwatch.elapsed(MILLISECONDS)).describedAs("Time taken should be less than 100ms").isLessThan(100);
	}
}

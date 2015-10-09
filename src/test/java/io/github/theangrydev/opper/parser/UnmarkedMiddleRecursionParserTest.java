package io.github.theangrydev.opper.parser;

import com.googlecode.yatspec.junit.Row;
import com.googlecode.yatspec.junit.Table;
import com.googlecode.yatspec.junit.TableRunner;
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.scanner.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;
import static java.lang.Boolean.valueOf;
import static java.lang.Integer.parseInt;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(TableRunner.class)
public class UnmarkedMiddleRecursionParserTest {

	@Table({
		@Row({"1", "false"}),
		@Row({"2", "true"}),
		@Row({"3", "false"}),
		@Row({"4", "true"}),
		@Row({"5", "false"}),
		@Row({"6", "true"})
	})
	@Test
	public void shouldRecogniseAGrammarWithAnUnmarkedMiddleRecursion(String repetitions, String shouldParse) {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "START", "REPEATED")
			.withRule("START", "REPEATED", "REPEATED")
			.build();
		Corpus corpus = corpus(nCopies(parseInt(repetitions), scannedSymbol(grammar.symbolByName("REPEATED"), "")));

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse().isPresent()).describedAs(repetitions + " should be " + shouldParse).isEqualTo(valueOf(shouldParse));
	}
}

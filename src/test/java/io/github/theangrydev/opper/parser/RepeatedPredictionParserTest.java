package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.scanner.FixedScanner;
import io.github.theangrydev.opper.scanner.Scanner;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import static io.github.theangrydev.opper.scanner.FixedScanner.scanner;
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
		Scanner scanner = FixedScanner.scanner(grammar, "BEGIN", "END");

		EarlyParser parser = new EarlyParser(new DoNothingLogger(), grammar, scanner);

		assertThat(parser.parse()).isPresent();
		assertThat(parser.finalEarlySetSize()).isEqualTo(6);
	}
}

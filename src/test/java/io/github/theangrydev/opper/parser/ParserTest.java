package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.scanner.FixedScanner;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.Scanner;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ParserTest {

	@Test
	public void shouldRecogniseASimpleGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "MIDDLE", "FIRST")
			.withRule("FIRST", "SECOND", "DUMMY")
			.build();
		Scanner scanner = FixedScanner.scanner(grammar, "MIDDLE", "SECOND", "DUMMY");

		Parser parser = new EarlyParser(new DoNothingLogger(), grammar, scanner);

		assertThat(parser.parse()).isPresent();
	}
}

package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.scanner.FixedScanner;
import io.github.theangrydev.opper.scanner.Scanner;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;

import static io.github.theangrydev.opper.scanner.FixedScanner.scanner;
import static org.assertj.core.api.Assertions.assertThat;

public class IndireclytRightRecursiveParserTest {

	@Test
	public void shouldRecogniseAnIndirectlyRightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "INDIRECT")
			.withRule("START", "REPEATED")
			.withRule("INDIRECT", "MIDDLE", "START")
			.build();
		Scanner scanner = FixedScanner.scanner(grammar, "REPEATED", "MIDDLE", "REPEATED", "MIDDLE", "REPEATED");

		EarlyParser parser = new EarlyParser(new DoNothingLogger(), grammar, scanner);

		assertThat(parser.parse()).isPresent();
	}
}

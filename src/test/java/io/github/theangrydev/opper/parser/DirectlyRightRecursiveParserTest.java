package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.Scanner;
import org.junit.Test;

import static io.github.theangrydev.opper.scanner.FixedScanner.scanner;
import static io.github.theangrydev.opper.scanner.Location.location;
import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

public class DirectlyRightRecursiveParserTest {

	@Test
	public void shouldRecogniseADirectlyRightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "REPEATED", "START")
			.withRule("START", "REPEATED")
			.build();
		Scanner scanner = scanner(nCopies(10, scannedSymbol(grammar.symbolByName("REPEATED"), "", location(1, 1, 1, 1))));

		Parser parser = new EarlyParserFactory(new DoNothingLogger(), grammar).parser(scanner);

		assertThat(parser.parse()).isPresent();
	}
}

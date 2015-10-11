package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.Scanner;
import org.junit.Test;

import java.io.IOException;

import static io.github.theangrydev.opper.scanner.FixedScanner.scanner;
import static io.github.theangrydev.opper.scanner.Location.location;
import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class LeftRecursiveParserTest {

	@Test
	public void shouldRecogniseALeftRecursiveGrammar() throws IOException, InterruptedException {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withRule("START", "START", "REPEATED")
			.withRule("START", "REPEATED")
			.build();
		Scanner scanner = scanner(nCopies(10, scannedSymbol(grammar.symbolByName("REPEATED"), "", location(1, 1, 1, 1))));

		Parser parser = new EarlyParser(new DoNothingLogger(), grammar, scanner);

		assertThat(parser.parse()).isPresent();
	}
}

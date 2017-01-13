/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.FixedScanner;
import io.github.theangrydev.opper.scanner.Scanner;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class RepeatedPredictionParserTest implements WithAssertions {

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

		EarlyParser parser = new EarlyParserFactory(new DoNothingLogger(), grammar).parser(scanner);

		assertThat(parser.parse()).isPresent();
		assertThat(parser.finalEarlySetSize()).isEqualTo(6);
	}
}

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

import com.googlecode.yatspec.junit.Row;
import com.googlecode.yatspec.junit.Table;
import com.googlecode.yatspec.junit.TableRunner;
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.Scanner;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.github.theangrydev.opper.scanner.FixedScanner.scanner;
import static io.github.theangrydev.opper.scanner.Location.location;
import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;
import static java.lang.Boolean.valueOf;
import static java.lang.Integer.parseInt;
import static java.util.Collections.nCopies;

@RunWith(TableRunner.class)
public class UnmarkedMiddleRecursionParserTest implements WithAssertions {

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
		Scanner scanner = scanner(nCopies(parseInt(repetitions), scannedSymbol(grammar.symbolByName("REPEATED"), "", location(1, 1, 1, 1))));

		Parser parser = new EarlyParserFactory(new DoNothingLogger(), grammar).parser(scanner);

		assertThat(parser.parse().isPresent()).describedAs(repetitions + " should be " + shouldParse).isEqualTo(valueOf(shouldParse));
	}
}

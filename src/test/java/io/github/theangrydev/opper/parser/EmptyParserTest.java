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

import com.google.common.base.Splitter;
import com.googlecode.yatspec.junit.Row;
import com.googlecode.yatspec.junit.Table;
import com.googlecode.yatspec.junit.TableRunner;
import io.github.theangrydev.opper.common.SystemOutLogger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.FixedScanner;
import io.github.theangrydev.opper.scanner.Scanner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.lang.Boolean.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(TableRunner.class)
public class EmptyParserTest {

	@Table({
		@Row({"", "true"}),
		@Row({"ant", "true"}),
		@Row({"ant bat", "true"})
	})
	@Test
	public void shouldRecogniseAGrammarWithAnEmptySymbol(String spaceSeperatedCorpus, String shouldParse) {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("LIST")
			.withEmptySymbol("EMPTY")
			.withRule("LIST", "EMPTY")
			.withRule("LIST", "LIST", "NAME")
			.withRule("NAME", "ant")
			.withRule("NAME", "bat")
			.withRule("NAME", "cow")
			.build();

		Scanner scanner = FixedScanner.scanner(grammar, Splitter.on(' ').omitEmptyStrings().split(spaceSeperatedCorpus));

		Parser parser = new EarlyParserFactory(new SystemOutLogger(), grammar).parser(scanner);

		assertThat(parser.parse().isPresent()).describedAs(spaceSeperatedCorpus + " should parse as " + shouldParse).isEqualTo(valueOf(shouldParse));
	}

	@Table({
		@Row({"( A B ; A B ; A B )", "true"}),
		@Row({"( A ; A B ; A B )", "false"}),
		@Row({"( A B ; A ; A B )", "false"}),
		@Row({"( A B ; A B ; A )", "false"}),
		@Row({"( A B ; A B ; )", "true"}),
		@Row({"( A ; A B ; )", "false"}),
		@Row({"( A B ; A ; )", "false"}),
		@Row({"( ; A B ; A B )", "true"}),
		@Row({"( ; A ; A B )", "false"}),
		@Row({"( ; A B ; A )", "false"}),
		@Row({"( A B ; ; A B )", "true"}),
		@Row({"( A ; ; A B )", "false"}),
		@Row({"( A B ; ; A )", "false"}),
		@Row({"( A B ; ; )", "true"}),
		@Row({"( A ; ; )", "false"}),
		@Row({"(  ; A B ; )", "true"}),
		@Row({"(  ; A ; )", "false"}),
		@Row({"(  ; ; A B )", "true"}),
		@Row({"(  ; ; A )", "false"}),
		@Row({"( ; ; )", "true"})
	})
	@Test
	public void shouldRecogniseAGrammarWithMultipleEmptySymbols(String spaceSeperatedCorpus, String shouldParse) {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("PROGRAM")
			.withEmptySymbol("EMPTY")
			.withRule("PROGRAM", "(", "LIST", ";", "LIST", ";", "LIST", ")")
			.withRule("LIST", "EMPTY")
			.withRule("LIST", "LIST", "ITEM")
			.withRule("ITEM", "A", "B")
			.build();

		Scanner scanner = FixedScanner.scanner(grammar, Splitter.on(' ').omitEmptyStrings().split(spaceSeperatedCorpus));

		Parser parser = new EarlyParserFactory(new SystemOutLogger(), grammar).parser(scanner);

		assertThat(parser.parse().isPresent()).describedAs(spaceSeperatedCorpus + " should parse as " + shouldParse).isEqualTo(valueOf(shouldParse));
	}
}

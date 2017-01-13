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
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.scanner.FixedScanner;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.Scanner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static java.lang.Boolean.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(TableRunner.class)
public class BNFParserTest {

	@Table({
		@Row({"( ant )", "true"}),
		@Row({"ant", "false"}),
		@Row({"( ant", "false"}),
		@Row({"ant )", "false"}),
		@Row({"( ant , bat )", "true"}),
		@Row({"( ant , bat", "false"}),
		@Row({"( ( ant )", "false"}),
		@Row({"( ( ant ) )", "true"}),
		@Row({"( ( ant , bat ) , cow )", "true"}),
	})
	@Test
	public void shouldRecogniseABNFGrammar(String spaceSeperatedCorpus, String shouldParse) throws IOException {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("TREE")
			.withRule("TREE", "(", "LIST", ")")
			.withRule("LIST", "THING")
			.withRule("LIST", "LIST", ",", "THING")
			.withRule("THING", "TREE")
			.withRule("THING", "NAME")
			.withRule("NAME", "ant")
			.withRule("NAME", "bat")
			.withRule("NAME", "cow")
			.build();
		Scanner scanner = FixedScanner.scanner(grammar, Splitter.on(' ').split(spaceSeperatedCorpus));

		Parser parser = new EarlyParserFactory(new DoNothingLogger(), grammar).parser( scanner);

		assertThat(parser.parse().isPresent()).isEqualTo(valueOf(shouldParse));
	}
}

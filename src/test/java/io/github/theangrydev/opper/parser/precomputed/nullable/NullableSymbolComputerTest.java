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
package io.github.theangrydev.opper.parser.precomputed.nullable;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class NullableSymbolComputerTest implements WithAssertions {

	@Test
	public void computesNullableSymbolsForGrammarWithNullableSymbols() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("TREE")
			.withEmptySymbol("NONE")
			.withRule("A", "B", "C")
			.withRule("A", "D", "C")
			.withRule("A'", "C")
			.withRule("C", "NONE")
			.withRule("D", "SOME")
			.build();

		NullableSymbolComputer computer = new NullableSymbolComputer(grammar, new CachingNullableSymbolParseTreeComputer(grammar));

		assertThat(computer.computeNullableSymbols().keySet()).containsOnlyElementsOf(grammar.symbolsByName("A'", "C"));
	}

	@Test
	public void computesNullableSymbolsForRightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("LIST")
			.withEmptySymbol("NONE")
			.withRule("LIST", "STATEMENT", "LIST")
			.withRule("LIST", "NONE")
			.build();

		NullableSymbolComputer computer = new NullableSymbolComputer(grammar, new CachingNullableSymbolParseTreeComputer(grammar));

		assertThat(computer.computeNullableSymbols().keySet()).containsOnlyElementsOf(grammar.symbolsByName("ACCEPT", "LIST"));
	}

	@Test
	public void computesNullableSymbolsForLeftRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("LIST")
			.withEmptySymbol("NONE")
			.withRule("LIST", "LIST", "STATEMENT")
			.withRule("LIST", "NONE")
			.build();

		NullableSymbolComputer computer = new NullableSymbolComputer(grammar, new CachingNullableSymbolParseTreeComputer(grammar));

		assertThat(computer.computeNullableSymbols().keySet()).containsOnlyElementsOf(grammar.symbolsByName("ACCEPT", "LIST"));
	}
}

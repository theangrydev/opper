/*
 * Copyright 2015 Liam Williams <liam.williams@zoho.com>.
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
import io.github.theangrydev.opper.parser.tree.ParseTree;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;

public class NullableSymbolParseTreesTest {

	@Test
	public void ambiguousNullParsesResultInAnException() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("TREE")
			.withEmptySymbol("NONE")
			.withRule("A", "B")
			.withRule("A", "C")
			.withRule("B", "NONE")
			.withRule("C", "NONE")
			.build();

		NullableSymbolComputer computer = new NullableSymbolComputer(grammar, new CachingNullableSymbolParseTreeComputer(grammar));

		assertThatThrownBy(computer::computeNullableSymbols)
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("A -> B[B -> NONE[]")
			.hasMessageContaining("A -> C[C -> NONE[]");
	}

	@Test
	public void parseTreeOneLevelDeepIsASingleNode() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("A")
			.withEmptySymbol("NONE")
			.withRule("A", "NONE")
			.build();

		NullableSymbolParseTrees nullableSymbolParseTrees = new NullableSymbolParseTrees(grammar, new NullableSymbolComputer(grammar, new CachingNullableSymbolParseTreeComputer(grammar)));

		Optional<ParseTree> parseTree = nullableSymbolParseTrees.nullableSymbolParseTree(grammar.symbolByName("A"));

		assertThat(parseTree.get()).hasToString("A -> NONE[]");
	}

	@Test
	public void parseTreeManyLevelsDeepHasNodeForEachRule() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("A")
			.withEmptySymbol("NONE")
			.withRule("A", "B")
			.withRule("B", "NONE")
			.build();

		NullableSymbolParseTrees nullableSymbolParseTrees = new NullableSymbolParseTrees(grammar, new NullableSymbolComputer(grammar, new CachingNullableSymbolParseTreeComputer(grammar)));

		Optional<ParseTree> parseTree = nullableSymbolParseTrees.nullableSymbolParseTree(grammar.symbolByName("A"));

		assertThat(parseTree.get()).hasToString("A -> B[B -> NONE[]]");
	}

	@Test
	public void parseTreeHasNodeForEachNullableChild() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("A")
			.withEmptySymbol("NONE")
			.withRule("A", "B", "C")
			.withRule("B", "NONE")
			.withRule("C", "NONE")
			.build();

		NullableSymbolParseTrees nullableSymbolParseTrees = new NullableSymbolParseTrees(grammar, new NullableSymbolComputer(grammar, new CachingNullableSymbolParseTreeComputer(grammar)));

		Optional<ParseTree> parseTree = nullableSymbolParseTrees.nullableSymbolParseTree(grammar.symbolByName("A"));

		assertThat(parseTree.get()).hasToString("A -> B C[B -> NONE[], C -> NONE[]]");
	}
}

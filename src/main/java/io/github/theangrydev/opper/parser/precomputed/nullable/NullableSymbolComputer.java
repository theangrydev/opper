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
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class NullableSymbolComputer {

	private final Grammar grammar;
	private final NullableSymbolParseTreeComputer nullableSymbolParseTreeComputer;

	public NullableSymbolComputer(Grammar grammar, NullableSymbolParseTreeComputer nullableSymbolParseTreeComputer) {
		this.grammar = grammar;
		this.nullableSymbolParseTreeComputer = nullableSymbolParseTreeComputer;
	}

	public Map<Symbol, ParseTree> computeNullableSymbols() {
		return grammar.symbols().stream()
			.map(nullableSymbolParseTreeComputer::nullParseTree)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(toMap(ParseTree::trigger, identity()));
	}
}

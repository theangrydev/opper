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
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.tree.ParseTree;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.Map;
import java.util.Optional;

public class NullableSymbolParseTrees {

	private final ObjectList<Optional<ParseTree>> nullableSymbols;

	public NullableSymbolParseTrees(Grammar grammar, NullableSymbolComputer nullableSymbolComputer) {
		int symbols = grammar.symbols().size();
		this.nullableSymbols = new ObjectArrayList<>(symbols);
		nullableSymbols.size(symbols);
		Map<Symbol, ParseTree> computedNullableSymbols = nullableSymbolComputer.computeNullableSymbols();
		for (Symbol symbol : grammar.symbols()) {
			nullableSymbols.set(symbol.id(), Optional.ofNullable(computedNullableSymbols.get(symbol)));
		}
	}

	public Optional<ParseTree> nullableSymbolParseTree(Symbol symbol) {
		return nullableSymbols.get(symbol.id());
	}
}

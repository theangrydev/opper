package io.github.theangrydev.opper.parser.precomputed.nullable;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;

import java.util.Set;

public class NullableSymbols {

	private final boolean[] nullableSymbols;

	public NullableSymbols(Grammar grammar, NullableSymbolComputer nullableSymbolComputer) {
		int symbols = grammar.symbols().size();
		this.nullableSymbols = new boolean[symbols];
		Set<Symbol> computedNullableSymbols = nullableSymbolComputer.computeNullableSymbols();
		for (Symbol symbol : grammar.symbols()) {
			nullableSymbols[symbol.id()] = computedNullableSymbols.contains(symbol);
		}
	}

	public boolean isNullable(Symbol symbol) {
		return nullableSymbols[symbol.id()];
	}
}

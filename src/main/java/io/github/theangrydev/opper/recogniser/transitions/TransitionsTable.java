package io.github.theangrydev.opper.recogniser.transitions;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class TransitionsTable {

	private final List<Symbol> symbols;
	private final ObjectList<TransitionsEarlySetsBySymbol> transitionsEarlySetsBySymbols;

	public TransitionsTable(Grammar grammar) {
		this.symbols = grammar.symbols();
		this.transitionsEarlySetsBySymbols = new ObjectArrayList<>();
	}

	public TransitionsEarlySetsBySymbol transitionsFromOrigin(int location) {
		return transitionsEarlySetsBySymbols.get(location);
	}

	public void expand() {
		transitionsEarlySetsBySymbols.add(new TransitionsEarlySetsBySymbol(symbols));
	}

	@Override
	public String toString() {
		return transitionsEarlySetsBySymbols.stream().map(Object::toString).collect(joining("\n", "\n", "\n"));
	}
}

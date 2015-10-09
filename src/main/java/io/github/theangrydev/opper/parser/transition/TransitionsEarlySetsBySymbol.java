package io.github.theangrydev.opper.parser.transition;

import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class TransitionsEarlySetsBySymbol {

	private final List<Symbol> symbols;
	private final List<TransitionsEarlySet> earlySets;

	public TransitionsEarlySetsBySymbol(List<Symbol> symbols) {
		this.symbols = symbols;
		this.earlySets = new ObjectArrayList<>(symbols.size());
		for (int i = 0; i < symbols.size(); i++) {
			earlySets.add(new TransitionsEarlySet());
		}
	}

	public TransitionsEarlySet itemsThatCanAdvanceGiven(Symbol symbol) {
		return earlySets.get(symbol.id());
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append('\n');
		for (Symbol symbol : symbols) {
			string.append(symbol);
			string.append(':');
			string.append(earlySets.get(symbol.id()));
			string.append('\n');
		}
		return string.toString();
	}
}

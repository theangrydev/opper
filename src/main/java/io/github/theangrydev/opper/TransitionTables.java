package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.lang.String;
import java.util.List;
import java.util.Set;

public class TransitionTables {

	private final List<Symbol> symbols;
	private final ObjectList<EarlyOrLeoSetsTable> earlyOrLeoSetsTables;

	public TransitionTables(List<Symbol> symbols, int corpusSize) {
		this.symbols = symbols;
		int size = corpusSize + 1;
		this.earlyOrLeoSetsTables = new ObjectArrayList<>(symbols.size());
		for (int i = 0; i < symbols.size(); i++) {
			earlyOrLeoSetsTables.add(new EarlyOrLeoSetsTable(size));
		}
	}

	public Set<EarlyOrLeoItem> transitions(Symbol symbol, int location) {
		return earlyOrLeoSetsTables.get(symbol.index()).earlySet(location);
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		for (Symbol symbol : symbols) {
			string.append(symbol);
			string.append(':');
			string.append(earlyOrLeoSetsTables.get(symbol.index()));
			string.append('\n');
		}
		return string.toString();
	}
}

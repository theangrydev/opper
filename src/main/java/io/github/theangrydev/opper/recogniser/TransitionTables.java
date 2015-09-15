package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.Collection;
import java.util.List;

public class TransitionTables {

	private final List<Symbol> symbols;
	private final ObjectList<EarlyOrLeoSetsTable> earlyOrLeoSetsTables;

	public TransitionTables(Grammar grammar) {
		this.symbols = grammar.symbols();
		this.earlyOrLeoSetsTables = new ObjectArrayList<>(symbols.size());
		for (int i = 0; i < symbols.size(); i++) {
			earlyOrLeoSetsTables.add(new EarlyOrLeoSetsTable());
		}
	}

	public Collection<EarlyOrLeoItem> transitions(Symbol symbol, int location) {
		return earlyOrLeoSetsTables.get(symbol.id()).earlySet(location);
	}

	public void expand() {
		earlyOrLeoSetsTables.forEach(EarlyOrLeoSetsTable::expand);
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append('\n');
		for (Symbol symbol : symbols) {
			string.append(symbol);
			string.append(':');
			string.append(earlyOrLeoSetsTables.get(symbol.id()));
			string.append('\n');
		}
		return string.toString();
	}
}

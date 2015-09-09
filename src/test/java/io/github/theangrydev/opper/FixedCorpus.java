package io.github.theangrydev.opper;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FixedCorpus implements Corpus {

	private final Iterator<Symbol> symbols;

	private FixedCorpus(Iterator<Symbol> symbols) {
		this.symbols = symbols;
	}

	public static FixedCorpus corpus(Grammar grammar, String... symbols) {
		return new FixedCorpus(Arrays.stream(symbols).map(grammar::symbolByName).iterator());
	}

	public static FixedCorpus corpus(Symbol... symbols) {
		return new FixedCorpus(Arrays.stream(symbols).iterator());
	}

	public static FixedCorpus corpus(List<Symbol> symbols) {
		return new FixedCorpus(symbols.iterator());
	}

	@Override
	public Symbol nextSymbol() {
		return symbols.next();
	}

	@Override
	public boolean hasNextSymbol() {
		return symbols.hasNext();
	}
}

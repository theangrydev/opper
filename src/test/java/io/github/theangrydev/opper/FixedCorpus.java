package io.github.theangrydev.opper;

import java.util.Arrays;
import java.util.Iterator;

public class FixedCorpus implements Corpus {

	private final Iterator<Symbol> symbols;

	public FixedCorpus(Symbol... symbols) {
		this.symbols = Arrays.stream(symbols).iterator();
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

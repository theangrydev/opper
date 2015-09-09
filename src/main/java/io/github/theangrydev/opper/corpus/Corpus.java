package io.github.theangrydev.opper.corpus;

import io.github.theangrydev.opper.grammar.Symbol;

public interface Corpus {
	Symbol nextSymbol();
	boolean hasNextSymbol();
}

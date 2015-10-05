package io.github.theangrydev.opper.corpus;

import io.github.theangrydev.opper.scanner.SymbolInstance;

public interface Corpus {
	SymbolInstance nextSymbol();
	boolean hasNextSymbol();
}

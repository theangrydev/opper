package io.github.theangrydev.opper.corpus;

import io.github.theangrydev.opper.scanner.ScannedSymbol;

public interface Corpus {
	ScannedSymbol nextSymbol();
	boolean hasNextSymbol();
}

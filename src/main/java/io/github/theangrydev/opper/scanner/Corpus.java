package io.github.theangrydev.opper.scanner;

public interface Corpus {
	ScannedSymbol nextSymbol();
	boolean hasNextSymbol();
}

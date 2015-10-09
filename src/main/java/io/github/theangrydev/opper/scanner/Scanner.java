package io.github.theangrydev.opper.scanner;

public interface Scanner {
	ScannedSymbol nextSymbol();
	boolean hasNextSymbol();
}

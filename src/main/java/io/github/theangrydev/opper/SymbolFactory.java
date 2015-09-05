package io.github.theangrydev.opper;

public class SymbolFactory {

	private int idSequence;

	public Symbol createSymbol(String name) {
		return new Symbol(idSequence++, name);
	}
}

package io.github.theangrydev.opper.grammar;

import java.util.HashSet;
import java.util.Set;

public class SymbolFactory {

	private final Set<String> usedNames;
	private int idSequence;

	public SymbolFactory() {
		this.usedNames = new HashSet<>();
	}

	public Symbol createSymbol(String name) {
		if (!usedNames.add(name)) {
			throw new IllegalArgumentException("Symbol name '" + name + "' is already used");
		}
		return new Symbol(idSequence++, name);
	}
}

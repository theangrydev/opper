package io.github.theangrydev.opper;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class String {

	private final List<Symbol> symbols;

	public String(Symbol... symbols) {
		this.symbols = Arrays.asList(symbols);
	}

	public int length() {
		return symbols.size();
	}

	public Symbol symbolAt(int dotPosition) {
		return symbols.get(dotPosition);
	}

	@Override
	public java.lang.String toString() {
		return symbols.stream().map(Symbol::toString).collect(joining(" "));
	}
}

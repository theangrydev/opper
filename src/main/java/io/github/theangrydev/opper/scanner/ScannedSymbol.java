package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.Symbol;

public class ScannedSymbol {
	private final Symbol symbol;
	private final Location location;
	private final String content;

	private ScannedSymbol(Symbol symbol, String content, Location location) {
		this.symbol = symbol;
		this.content = content;
		this.location = location;
	}

	public static ScannedSymbol scannedSymbol(Symbol symbol, String content, Location location) {
		return new ScannedSymbol(symbol, content, location);
	}

	public Symbol symbol() {
		return symbol;
	}

	public String content() {
		return content;
	}

	public Location location() {
		return location;
	}

	@Override
	public String toString() {
		return "ScannedSymbol{" +
			"symbol=" + symbol +
			", location=" + location +
			", content='" + content + '\'' +
			'}';
	}
}

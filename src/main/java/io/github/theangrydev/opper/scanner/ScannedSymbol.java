package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.Symbol;

public class ScannedSymbol {
	private final Symbol symbol;
	private final String content;

	private ScannedSymbol(Symbol symbol, String content) {
		this.symbol = symbol;
		this.content = content;
	}

	public static ScannedSymbol scannedSymbol(Symbol symbol, String content) {
		return new ScannedSymbol(symbol, content);
	}

	public Symbol symbol() {
		return symbol;
	}

	public String content() {
		return content;
	}
}

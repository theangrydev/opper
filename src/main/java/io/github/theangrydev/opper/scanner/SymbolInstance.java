package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.Symbol;

public class SymbolInstance {

	private final Symbol symbol;
	private final String content;

	private SymbolInstance(Symbol symbol, String content) {
		this.symbol = symbol;
		this.content = content;
	}

	public static SymbolInstance symbolInstance(Symbol symbol, String content) {
		return new SymbolInstance(symbol, content);
	}

	public Symbol symbol() {
		return symbol;
	}

	public String content() {
		return content;
	}
}

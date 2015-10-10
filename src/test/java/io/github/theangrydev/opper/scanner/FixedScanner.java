package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.common.Streams;
import io.github.theangrydev.opper.grammar.Grammar;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;

public class FixedScanner implements Scanner {

	private final Iterator<ScannedSymbol> symbols;

	private FixedScanner(Iterator<ScannedSymbol> symbols) {
		this.symbols = symbols;
	}

	public static FixedScanner scanner(Grammar grammar, String... symbols) {
		return scanner(grammar, Arrays.asList(symbols));
	}

	public static FixedScanner scanner(Grammar grammar, Iterable<String> symbols) {
		return new FixedScanner(Streams.stream(symbols).map(symbol -> scannedSymbol(grammar.symbolByName(symbol), symbol)).iterator());
	}

	public static FixedScanner scanner(ScannedSymbol... symbols) {
		return new FixedScanner(Arrays.stream(symbols).iterator());
	}

	public static FixedScanner scanner(List<ScannedSymbol> symbols) {
		return new FixedScanner(symbols.iterator());
	}

	@Override
	public ScannedSymbol nextSymbol() {
		return symbols.next();
	}

	@Override
	public boolean hasNextSymbol() {
		return symbols.hasNext();
	}
}

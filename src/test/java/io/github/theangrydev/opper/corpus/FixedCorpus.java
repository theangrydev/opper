package io.github.theangrydev.opper.corpus;

import io.github.theangrydev.opper.common.Streams;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.scanner.Corpus;
import io.github.theangrydev.opper.scanner.ScannedSymbol;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;

public class FixedCorpus implements Corpus {

	private final Iterator<ScannedSymbol> symbols;

	private FixedCorpus(Iterator<ScannedSymbol> symbols) {
		this.symbols = symbols;
	}

	public static FixedCorpus corpus(Grammar grammar, String... symbols) {
		return corpus(grammar, Arrays.asList(symbols));
	}

	public static FixedCorpus corpus(Grammar grammar, Iterable<String> symbols) {
		return new FixedCorpus(Streams.stream(symbols).map(symbol -> scannedSymbol(grammar.symbolByName(symbol), symbol)).iterator());
	}

	public static FixedCorpus corpus(ScannedSymbol... symbols) {
		return new FixedCorpus(Arrays.stream(symbols).iterator());
	}

	public static FixedCorpus corpus(List<ScannedSymbol> symbols) {
		return new FixedCorpus(symbols.iterator());
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

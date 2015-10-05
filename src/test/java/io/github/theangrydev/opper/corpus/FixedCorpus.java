package io.github.theangrydev.opper.corpus;

import io.github.theangrydev.opper.common.Streams;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.scanner.SymbolInstance;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static io.github.theangrydev.opper.scanner.SymbolInstance.symbolInstance;

public class FixedCorpus implements Corpus {

	private final Iterator<SymbolInstance> symbols;

	private FixedCorpus(Iterator<SymbolInstance> symbols) {
		this.symbols = symbols;
	}

	public static FixedCorpus corpus(Grammar grammar, String... symbols) {
		return corpus(grammar, Arrays.asList(symbols));
	}

	public static FixedCorpus corpus(Grammar grammar, Iterable<String> symbols) {
		return new FixedCorpus(Streams.stream(symbols).map(symbol -> symbolInstance(grammar.symbolByName(symbol), symbol)).iterator());
	}

	public static FixedCorpus corpus(SymbolInstance... symbols) {
		return new FixedCorpus(Arrays.stream(symbols).iterator());
	}

	public static FixedCorpus corpus(List<SymbolInstance> symbols) {
		return new FixedCorpus(symbols.iterator());
	}

	@Override
	public SymbolInstance nextSymbol() {
		return symbols.next();
	}

	@Override
	public boolean hasNextSymbol() {
		return symbols.hasNext();
	}
}

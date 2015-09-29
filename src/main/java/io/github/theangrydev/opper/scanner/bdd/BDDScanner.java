package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.NFA;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.util.List;
import java.util.Optional;

public class BDDScanner implements Corpus {

	private final char[] charactersToParse;
	private Symbol next;
	private int index;
	private final BFA bfa;

	public BDDScanner(List<SymbolDefinition> symbolDefinitions, char...  charactersToParse) {
		this.charactersToParse = charactersToParse;
		NFA nfa = NFA.convertToNFA(symbolDefinitions);
		nfa.removeEpsilionTransitions();
		nfa.removeUnreachableStates();
		nfa.relabelAccordingToFrequencies();

		bfa = new BFA(nfa);
	}

	@Override
	public Symbol nextSymbol() {
		return next;
	}

	@Override
	public boolean hasNextSymbol() {
		while (index < charactersToParse.length) {
			System.out.println("index=" + index);
			char character = charactersToParse[index++];

			Optional<Symbol> next = bfa.scan(character);
			if (next.isPresent()) {
				this.next = next.get();
				return true;
			}
		}
		return false;
	}
}

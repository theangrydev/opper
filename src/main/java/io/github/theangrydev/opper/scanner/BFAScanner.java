package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.bfa.BFA;
import io.github.theangrydev.opper.scanner.automaton.bfa.BFABuilder;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFABuilder;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;

public class BFAScanner implements Scanner {

	private final Reader charactersToParse;
	private final BFA bfa;
	private BinaryDecisionDiagram frontier;
	private ScannedSymbol next;
	private StringBuilder nextCharacters;

	public BFAScanner(List<SymbolDefinition> symbolDefinitions, Reader charactersToParse) {
		this.charactersToParse = charactersToParse;
		NFA nfa = NFABuilder.convertToNFA(symbolDefinitions);
		nfa.removeEpsilionTransitions();
		nfa.removeUnreachableStates();
		nfa.relabelAccordingToFrequencies();

		bfa = BFABuilder.convertToBFA(nfa);
		prepareForNextSymbol();
	}

	@Override
	public ScannedSymbol nextSymbol() {
		return next;
	}

	@Override
	public boolean hasNextSymbol() {
		for (int read = read(); read != -1; read = read()) {
			char character = (char) read;
			nextCharacters.append(character);

			frontier = bfa.transition(frontier, character);
			if (frontier.isZero()) {
				prepareForNextSymbol();
				continue;
			}
			Optional<Symbol> acceptedSymbol = bfa.checkAcceptance(frontier);
			if (acceptedSymbol.isPresent()) {
				next = scannedSymbol(acceptedSymbol.get(), nextCharacters.toString());
				prepareForNextSymbol();
				return true;
			}
			frontier = bfa.relabelToStateToFromState(frontier);
		}
		return false;
	}

	private void prepareForNextSymbol() {
		nextCharacters = new StringBuilder();
		frontier = bfa.initialState();
	}

	private int read() {
		try {
			return charactersToParse.read();
		} catch (IOException e) {
			return -1;
		}
	}
}

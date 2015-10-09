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

public class Scanner implements Corpus {

	private final Reader charactersToParse;
	private final BFA bfa;
	private BinaryDecisionDiagram frontier;
	private ScannedSymbol next;
	private StringBuilder nextCharacters;

	public Scanner(List<SymbolDefinition> symbolDefinitions, Reader charactersToParse) {
		this.charactersToParse = charactersToParse;
		NFA nfa = NFABuilder.convertToNFA(symbolDefinitions);
		nfa.removeEpsilionTransitions();
		nfa.removeUnreachableStates();
		nfa.relabelAccordingToFrequencies();

		bfa = BFABuilder.convertToBFA(nfa);
		frontier = bfa.initialState();
		nextCharacters = new StringBuilder();
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

			BinaryDecisionDiagram transitionTo = bfa.transition(frontier, character);
			Optional<Symbol> acceptedSymbol = bfa.checkAcceptance(transitionTo);
			frontier = bfa.relabelToStateToFromState(transitionTo);
			if (acceptedSymbol.isPresent()) {
				next = scannedSymbol(acceptedSymbol.get(), nextCharacters.toString());
				nextCharacters = new StringBuilder();
				return true;
			}
		}
		return false;
	}

	private int read() {
		try {
			return charactersToParse.read();
		} catch (IOException e) {
			return -1;
		}
	}
}

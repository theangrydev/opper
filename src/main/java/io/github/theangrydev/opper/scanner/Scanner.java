package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.corpus.Corpus;
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

public class Scanner implements Corpus {

	private final Reader charactersToParse;
	private final BFA bfa;
	private BinaryDecisionDiagram frontier;
	private Symbol next;

	public Scanner(List<SymbolDefinition> symbolDefinitions, Reader charactersToParse) {
		this.charactersToParse = charactersToParse;
		NFA nfa = NFABuilder.convertToNFA(symbolDefinitions);
		nfa.removeEpsilionTransitions();
		nfa.removeUnreachableStates();
		nfa.relabelAccordingToFrequencies();

		bfa = BFABuilder.convertToBFA(nfa);
		frontier = bfa.initialState();
	}

	@Override
	public Symbol nextSymbol() {
		return next;
	}

	@Override
	public boolean hasNextSymbol() {
		for (int character = nextCharacter(); character != -1; character = nextCharacter()) {
			BinaryDecisionDiagram transitionTo = bfa.transition(frontier, (char) character);
			Optional<Symbol> acceptedSymbol = bfa.checkAcceptance(transitionTo);
			frontier = bfa.relabelToStateToFromState(transitionTo);
			if (acceptedSymbol.isPresent()) {
				next = acceptedSymbol.get();
				return true;
			}
		}
		return false;
	}

	private int nextCharacter() {
		try {
			return charactersToParse.read();
		} catch (IOException e) {
			return -1;
		}
	}
}

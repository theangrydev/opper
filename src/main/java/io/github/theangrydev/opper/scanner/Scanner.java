package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.bfa.BFA;
import io.github.theangrydev.opper.scanner.automaton.bfa.BFABuilder;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFABuilder;
import io.github.theangrydev.opper.scanner.bdd.BDDVariable;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.util.List;
import java.util.Optional;

public class Scanner implements Corpus {

	private final char[] charactersToParse;
	private final BFA bfa;
	private BDDVariable frontier;
	private Symbol next;
	private int index;

	public Scanner(List<SymbolDefinition> symbolDefinitions, char... charactersToParse) {
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
		while (index < charactersToParse.length) {
			char character = charactersToParse[index++];
			BDDVariable transitionTo = bfa.transition(frontier, character);
			Optional<Symbol> acceptedSymbol = bfa.checkAcceptance(transitionTo);
			frontier = bfa.relabelToStateToFromState(transitionTo);
			if (acceptedSymbol.isPresent()) {
				this.next = acceptedSymbol.get();
				return true;
			}
		}
		return false;
	}
}

package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.NFA;
import io.github.theangrydev.opper.scanner.bdd.BDDVariable;
import io.github.theangrydev.opper.scanner.bdd.BDDVariableAssignment;
import io.github.theangrydev.opper.scanner.bdd.BFA;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.Optional;

public class Scanner implements Corpus {

	private final char[] charactersToParse;
	private final BFA bfa;
	private final BDDVariable existsFromStateAndCharacter;
	private final Permutation relabelToStateToFromState;
	private BDDVariable frontier;
	private Symbol next;
	private int index;

	public Scanner(List<SymbolDefinition> symbolDefinitions, char... charactersToParse) {
		this.charactersToParse = charactersToParse;
		NFA nfa = NFA.convertToNFA(symbolDefinitions);
		nfa.removeEpsilionTransitions();
		nfa.removeUnreachableStates();
		nfa.relabelAccordingToFrequencies();

		bfa = BFA.convertToBFA(nfa);
		existsFromStateAndCharacter = bfa.existsFromStateAndCharacter();
		relabelToStateToFromState = bfa.relabelToStateToFromState();
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
			Optional<Symbol> next = scan(character);
			if (next.isPresent()) {
				this.next = next.get();
				return true;
			}
		}
		return false;
	}

	private Optional<Symbol> scan(char character) {
		frontier = bfa.transition(frontier, character);

		Optional<Symbol> next = Optional.empty();
		BDDVariable acceptCheck = bfa.checkAcceptance(frontier);
		boolean accepted = acceptCheck.isNotZero();
		if (accepted) {
			BDDVariableAssignment satisfyingAssignment = acceptCheck.oneSatisfyingAssignment();
			Symbol acceptedSymbol = bfa.symbolForAssignment(satisfyingAssignment);
			next = Optional.of(acceptedSymbol);
		}
		acceptCheck.discard();
		frontier = frontier.replaceTo(relabelToStateToFromState);
		return next;
	}
}

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

		bfa = new BFA(nfa);

		existsFromStateAndCharacter = bfa.existsFromStateAndCharacter();
		relabelToStateToFromState = bfa.relabelToStateToFromState();
		frontier = bfa.startState();
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

			Optional<Symbol> next = scan(character);
			if (next.isPresent()) {
				this.next = next.get();
				return true;
			}
		}
		return false;
	}

	public Optional<Symbol> scan(char character) {
		frontier = frontier.andTo(bfa.transitionBddTable());
		frontier = frontier.andTo(bfa.characterBddSet(character));
		frontier = frontier.existsTo(existsFromStateAndCharacter);
		BDDVariable acceptCheck = bfa.acceptanceBddSet().and(frontier);
		frontier = frontier.replaceTo(relabelToStateToFromState);

		boolean accepted = acceptCheck.isZero();
		if (accepted) {
			BDDVariableAssignment assignment = acceptCheck.oneSatisfyingAssignment();
			acceptCheck.discard();
			int stateIndex = bfa.lookupToState(assignment);
			Symbol acceptedSymbol = bfa.symbolForStateIndex(stateIndex);
			return Optional.of(acceptedSymbol);
		}
		acceptCheck.discard();
		return Optional.empty();
	}
}

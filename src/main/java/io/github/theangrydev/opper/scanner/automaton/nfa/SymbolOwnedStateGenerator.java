package io.github.theangrydev.opper.scanner.automaton.nfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.definition.CharacterClass;

public class SymbolOwnedStateGenerator {

	private final Symbol createdBy;
	private final StateFactory stateFactory;
	private final TransitionFactory transitionFactory;

	public SymbolOwnedStateGenerator(Symbol createdBy, StateFactory stateFactory, TransitionFactory transitionFactory) {
		this.createdBy = createdBy;
		this.stateFactory = stateFactory;
		this.transitionFactory = transitionFactory;
	}

	public State newState() {
		return stateFactory.stateCreatedBy(createdBy);
	}

	public Transition characterTransition(char character) {
		return transitionFactory.characterTransition(character);
	}

	public Transition characterClassTransition(CharacterClass characterClass) {
		return transitionFactory.characterClassTransition(characterClass);
	}
}

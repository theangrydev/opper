package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.SymbolOwnedStateGenerator;

public class CharacterClassExpression implements Expression {
	private final CharacterClass characterClass;

	private CharacterClassExpression(CharacterClass characterClass) {
		this.characterClass = characterClass;
	}

	public static CharacterClassExpression characterClass(CharacterClass characterClass) {
		return new CharacterClassExpression(characterClass);
	}

	@Override
	public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
		from.addTransition(generator.characterClassTransition(characterClass), to);
	}

	@Override
	public String toString() {
		return String.valueOf(characterClass);
	}
}

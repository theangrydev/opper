package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.SymbolOwnedStateGenerator;

public class CharacterExpression implements Expression {
	private final char character;

	private CharacterExpression(char character) {
		this.character = character;
	}

	public static CharacterExpression character(char character) {
		return new CharacterExpression(character);
	}

	@Override
	public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
		from.addTransition(generator.characterTransition(character), to);
	}

	@Override
	public String toString() {
		return String.valueOf(character);
	}
}

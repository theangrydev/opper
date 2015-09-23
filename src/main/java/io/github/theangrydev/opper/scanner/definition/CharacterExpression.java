package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.SymbolOwnedStateGenerator;

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
		from.addTransition(character, to);
	}

	@Override
	public String toString() {
		return String.valueOf(character);
	}
}

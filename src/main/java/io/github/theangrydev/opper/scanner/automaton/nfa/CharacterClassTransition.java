package io.github.theangrydev.opper.scanner.automaton.nfa;

import io.github.theangrydev.opper.scanner.definition.CharacterClass;

public class CharacterClassTransition implements Transition {
	private int id;
	private final CharacterClass characterClass;

	public CharacterClassTransition(int id, CharacterClass characterClass) {
		this.id = id;
		this.characterClass = characterClass;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public void label(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "[" + id + "]" + characterClass;
	}

	public CharacterClass characterClass() {
		return characterClass;
	}
}

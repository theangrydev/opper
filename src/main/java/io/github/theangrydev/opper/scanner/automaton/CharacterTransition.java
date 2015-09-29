package io.github.theangrydev.opper.scanner.automaton;

public class CharacterTransition implements Transition {

	private final char character;
	private int id;

	public CharacterTransition(int id, char character) {
		this.id = id;
		this.character = character;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public void label(int id) {
		this.id = id;
	}

	public char character() {
		return character;
	}

	@Override
	public String toString() {
		return "[" + id + "]" + character;
	}
}

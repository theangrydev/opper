package io.github.theangrydev.opper.scanner.definition;

public class AnyCharacter implements CharacterClass {

	@Override
	public boolean contains(char character) {
		return true;
	}

	public static AnyCharacter anyCharacter() {
		return new AnyCharacter();
	}
}

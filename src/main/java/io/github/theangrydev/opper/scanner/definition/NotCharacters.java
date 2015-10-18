package io.github.theangrydev.opper.scanner.definition;

public class NotCharacters implements CharacterClass {

	private final String notCharacters;

	private NotCharacters(String notCharacters) {
		this.notCharacters = notCharacters;
	}

	public static NotCharacters notCharacaters(String notCharacters) {
		return new NotCharacters(notCharacters);
	}

	@Override
	public boolean contains(char character) {
		return notCharacters.indexOf(character) == -1;
	}
}

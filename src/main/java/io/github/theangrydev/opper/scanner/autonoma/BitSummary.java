package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.math.IntMath;

import java.math.RoundingMode;

public class BitSummary {

	private final int bitsForStates;
	private final int bitsForCharacters;
	private final int bitsPerRow;

	public BitSummary(int numberOfStates, int numberOfCharacters) {
		bitsForStates = IntMath.log2(numberOfStates, RoundingMode.FLOOR) + 1;
		bitsForCharacters = IntMath.log2(numberOfCharacters, RoundingMode.FLOOR) + 1;
		bitsPerRow = bitsForStates * 2 + bitsForCharacters;
	}

	public int minCharacterVariable() {
		return bitsForStates + 1;
	}

	public int maxCharacterVariable() {
		return minCharacterVariable() + bitsForCharacters;
	}

	private int maxToStateVariable() {
		return minToStateVariable() + bitsForStates;
	}

	private int minToStateVariable() {
		return maxCharacterVariable();
	}

	public int bitsForStates() {
		return bitsForStates;
	}

	public int bitsForCharacters() {
		return bitsForCharacters;
	}

	public int bitsPerRow() {
		return bitsPerRow;
	}

	@Override
	public String toString() {
		return "BitSummary{" +
			"bitsForStates=" + bitsForStates +
			", bitsForCharacters=" + bitsForCharacters +
			", bitsPerRow=" + bitsPerRow +
			'}';
	}

	public int projectFromId(int fromId) {
		return fromId;
	}

	public int projectCharacterId(int characterId) {
		return characterId << bitsForStates();
	}

	public int projectToId(int toId) {
		return toId << (bitsForStates() + bitsForCharacters());
	}

	public boolean isCharacter(Variable variable) {
		return variable.id() >= minCharacterVariable() && variable.id() < maxCharacterVariable();
	}

	public boolean isToState(Variable variable) {
		return variable.id() >= minToStateVariable() && variable.id() < maxToStateVariable();
	}
}

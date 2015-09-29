package io.github.theangrydev.opper.scanner.automaton.bfa;

import com.google.common.math.IntMath;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.Transition;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.math.RoundingMode;
import java.util.List;

public class VariableSummary {

	private final int bitsForStates;
	private final int bitsForCharacters;
	private final int bitsPerRow;

	public VariableSummary(int numberOfStates, int numberOfCharacters) {
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

	private int maxFromStateVariable() {
		return minFromStateVariable() + bitsForStates;
	}

	private int minFromStateVariable() {
		return 1;
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

	public int projectFromId(State from) {
		return from.id();
	}

	public int projectCharacterId(Transition transition) {
		return transition.id() << bitsForStates();
	}

	public int projectToId(State to) {
		return to.id() << (bitsForStates() + bitsForCharacters());
	}

	public int unprojectToIdBitPosition(int position) {
		return position - minToStateVariable();
	}

	public boolean isCharacter(VariableOrder variableOrder) {
		return variableOrder.id() >= minCharacterVariable() && variableOrder.id() < maxCharacterVariable();
	}

	public boolean isToState(VariableOrder variableOrder) {
		return variableOrder.id() >= minToStateVariable() && variableOrder.id() < maxToStateVariable();
	}

	public boolean isFromState(VariableOrder variableOrder) {
		return variableOrder.id() >= minFromStateVariable() && variableOrder.id() < maxFromStateVariable();
	}

	public boolean isFromStateOrCharacter(VariableOrder variableOrder) {
		return isFromState(variableOrder) || isCharacter(variableOrder);
	}

	public IntSet allVariableIds() {
		IntSet allVariables = new IntOpenHashSet(bitsPerRow);
		for (int i = 1; i <= bitsPerRow; i++) {
			allVariables.add(i);
		}
		return allVariables;
	}

	public boolean[] presentVariables(List<Integer> presentVariableIndexes) {
		boolean[] presentVariables = new boolean[bitsPerRow()];
		for (int presentVariableIndex : presentVariableIndexes) {
			presentVariables[presentVariableIndex] = true;
		}
		return presentVariables;
	}
}

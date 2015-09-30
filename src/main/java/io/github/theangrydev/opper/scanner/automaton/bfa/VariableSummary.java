package io.github.theangrydev.opper.scanner.automaton.bfa;

import com.google.common.math.IntMath;
import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.Transition;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.math.RoundingMode;
import java.util.BitSet;
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

	public VariablesSet variablesSetInTransition(State from, Transition via, State to) {
		BitSet setVariables = new BitSet(bitsPerRow());
		blastBits(projectFromId(from), setVariables);
		blastBits(projectCharacterId(via), setVariables);
		blastBits(projectToId(to), setVariables);
		return new VariablesSet(setVariables);
	}

	public VariablesSet variablesSetForToState(State state) {
		return new VariablesSet(BitSet.valueOf(new long[]{projectToId(state)}));
	}

	public VariablesSet variablesSetForFromState(State state) {
		return new VariablesSet(BitSet.valueOf(new long[]{projectFromId(state)}));
	}

	public VariablesSet variablesSetForCharacter(CharacterTransition characterTransition) {
		return new VariablesSet(BitSet.valueOf(new long[]{projectCharacterId(characterTransition)}));
	}

	private static void blastBits(long number, BitSet row) {
		row.or(BitSet.valueOf(new long[]{number}));
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

	private int projectFromId(State from) {
		return from.id();
	}

	private int projectCharacterId(Transition transition) {
		return transition.id() << bitsForStates();
	}

	private int projectToId(State to) {
		return to.id() << (bitsForStates() + bitsForCharacters());
	}

	public int unprojectToIdBitPosition(int position) {
		return position - minToStateVariable();
	}

	public boolean isCharacter(Variable variable) {
		return variable.id() >= minCharacterVariable() && variable.id() < maxCharacterVariable();
	}

	public boolean isToState(Variable variable) {
		return variable.id() >= minToStateVariable() && variable.id() < maxToStateVariable();
	}

	public boolean isFromState(Variable variable) {
		return variable.id() >= minFromStateVariable() && variable.id() < maxFromStateVariable();
	}

	public boolean isFromStateOrCharacter(Variable variable) {
		return isFromState(variable) || isCharacter(variable);
	}

	public IntSet allVariableIds() {
		IntSet allVariables = new IntOpenHashSet(bitsPerRow);
		for (int i = 1; i <= bitsPerRow; i++) {
			allVariables.add(i);
		}
		return allVariables;
	}

	public boolean[] presentVariables(List<Variable> presentVariables) {
		boolean[] variables = new boolean[bitsPerRow()];
		for (Variable presentVariable : presentVariables) {
			variables[presentVariable.order()] = true;
		}
		return variables;
	}
}

package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.Transition;

import java.util.BitSet;

public class SetVariables {

	private final BitSet setVariables;

	private SetVariables(BitSet setVariables) {
		this.setVariables = setVariables;
	}

	public static SetVariables transition(VariableSummary variableSummary, State from, Transition via, State to) {
		BitSet setVariables = new BitSet(variableSummary.bitsPerRow());
		blastBits(variableSummary.projectFromId(from), setVariables);
		blastBits(variableSummary.projectCharacterId(via), setVariables);
		blastBits(variableSummary.projectToId(to), setVariables);
		return new SetVariables(setVariables);
	}

	public static SetVariables toState(VariableSummary variableSummary, State state) {
		return new SetVariables(BitSet.valueOf(new long[]{variableSummary.projectToId(state)}));
	}

	public static SetVariables fromState(VariableSummary variableSummary, State state) {
		return new SetVariables(BitSet.valueOf(new long[]{variableSummary.projectFromId(state)}));
	}

	public static SetVariables character(VariableSummary variableSummary, CharacterTransition characterTransition) {
		return new SetVariables(BitSet.valueOf(new long[]{variableSummary.projectCharacterId(characterTransition)}));
	}

	private static void blastBits(long number, BitSet row) {
		row.or(BitSet.valueOf(new long[]{number}));
	}

	public boolean contains(Variable variable) {
		return contains(variable.id());
	}

	public boolean contains(int variableId) {
		return setVariables.get(variableId - 1);
	}
}

package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.scanner.bdd.BitSummary;
import io.github.theangrydev.opper.scanner.bdd.Variable;

import java.util.BitSet;

public class SetVariables {

	private final BitSet setVariables;

	private SetVariables(BitSet setVariables) {
		this.setVariables = setVariables;
	}

	public static SetVariables transition(BitSummary bitSummary, State from, Transition via, State to) {
		BitSet setVariables = new BitSet(bitSummary.bitsPerRow());
		blastBits(bitSummary.projectFromId(from), setVariables);
		blastBits(bitSummary.projectCharacterId(via), setVariables);
		blastBits(bitSummary.projectToId(to), setVariables);
		return new SetVariables(setVariables);
	}

	public static SetVariables toState(BitSummary bitSummary, State state) {
		return new SetVariables(BitSet.valueOf(new long[]{bitSummary.projectToId(state)}));
	}

	public static SetVariables fromState(BitSummary bitSummary, State state) {
		return new SetVariables(BitSet.valueOf(new long[]{bitSummary.projectFromId(state)}));
	}

	public static SetVariables character(BitSummary bitSummary, CharacterTransition characterTransition) {
		return new SetVariables(BitSet.valueOf(new long[]{bitSummary.projectCharacterId(characterTransition)}));
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

package io.github.theangrydev.opper.scanner.automaton.bfa;

import java.util.BitSet;

public class VariablesSet {

	private final BitSet setVariables;

	public VariablesSet(BitSet setVariables) {
		this.setVariables = setVariables;
	}

	public boolean contains(Variable variable) {
		return contains(variable.id());
	}

	public boolean contains(int variableId) {
		return setVariables.get(variableId - 1);
	}
}

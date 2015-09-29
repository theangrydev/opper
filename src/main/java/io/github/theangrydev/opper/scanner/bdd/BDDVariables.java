package io.github.theangrydev.opper.scanner.bdd;

import java.util.ArrayList;
import java.util.List;

public class BDDVariables {

	private final List<BDDVariable> bddVariables;
	private final List<BDDVariable> bddNotVariables;

	public BDDVariables(int numberOfVariables, BDDVariableFactory bddVariableFactory) {
		bddVariables = new ArrayList<>(numberOfVariables);
		for (int i = 0; i < numberOfVariables; i++) {
			bddVariables.add(bddVariableFactory.newVariable());
		}

		bddNotVariables = new ArrayList<>(numberOfVariables);
		for (int i = 0; i < numberOfVariables; i++) {
			bddNotVariables.add(bddVariables.get(i).not());
		}
	}

	public BDDVariable variable(int variableIndex) {
		return bddVariables.get(variableIndex);
	}

	public BDDVariable notVariable(int variableIndex) {
		return bddNotVariables.get(variableIndex);
	}
}

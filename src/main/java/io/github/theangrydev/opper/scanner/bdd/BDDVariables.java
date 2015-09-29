package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.VariableOrdering;

import java.util.ArrayList;
import java.util.List;

public class BDDVariables {

	private final List<BDDVariable> bddVariables;
	private final List<BDDVariable> bddNotVariables;

	public BDDVariables(VariableOrdering variableOrdering, BDDVariableFactory bddVariableFactory) {
		System.out.println("variable order=" + variableOrdering);
		bddVariables = new ArrayList<>(variableOrdering.numberOfVariables());
		for (int i = 0; i < variableOrdering.numberOfVariables(); i++) {
			bddVariables.add(bddVariableFactory.newVariable());
		}
		System.out.println("bddVariables=" + bddVariables);

		bddNotVariables = new ArrayList<>(variableOrdering.numberOfVariables());
		for (int i = 0; i < variableOrdering.numberOfVariables(); i++) {
			bddNotVariables.add(bddVariables.get(i).not());
		}
		System.out.println("bddNotVariables=" + bddNotVariables);
	}

	public BDDVariable variable(int variableIndex) {
		return bddVariables.get(variableIndex);
	}

	public BDDVariable notVariable(int variableIndex) {
		return bddNotVariables.get(variableIndex);
	}
}

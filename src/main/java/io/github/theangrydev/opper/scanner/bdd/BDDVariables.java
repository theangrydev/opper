package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.VariableOrdering;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import jdd.bdd.BDD;

public class BDDVariables {

	private final IntList bddVariables;
	private final IntList bddNotVariables;

	public BDDVariables(BDD bdd, VariableOrdering variableOrdering) {
		System.out.println("variable order=" + variableOrdering);
		bddVariables = new IntArrayList(variableOrdering.numberOfVariables());
		for (int i = 0; i < variableOrdering.numberOfVariables(); i++) {
			bddVariables.add(bdd.createVar());
		}
		System.out.println("bddVariables=" + bddVariables);

		bddNotVariables = new IntArrayList(variableOrdering.numberOfVariables());
		for (int i = 0; i < variableOrdering.numberOfVariables(); i++) {
			bddNotVariables.add(bdd.not(bddVariables.getInt(i)));
		}
		System.out.println("bddNotVariables=" + bddNotVariables);
	}

	public int variable(int variableIndex) {
		return bddVariables.getInt(variableIndex);
	}

	public int notVariable(int variableIndex) {
		return bddNotVariables.getInt(variableIndex);
	}
}

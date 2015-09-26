package io.github.theangrydev.opper.scanner.autonoma;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import jdd.bdd.BDD;

import java.util.List;

public class BDDVariables {

	private final IntList bddVariables;
	private final IntList bddNotVariables;

	public BDDVariables(BDD bdd, List<Variable> variables) {
		System.out.println("variable order=" + variables);
		bddVariables = new IntArrayList(variables.size());
		for (int i = 0; i < variables.size(); i++) {
			bddVariables.add(bdd.createVar());
		}
		System.out.println("bddVariables=" + bddVariables);

		bddNotVariables = new IntArrayList(variables.size());
		for (int i = 0; i < variables.size(); i++) {
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

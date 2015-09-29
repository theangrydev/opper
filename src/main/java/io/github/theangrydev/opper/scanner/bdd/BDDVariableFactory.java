package io.github.theangrydev.opper.scanner.bdd;

import jdd.bdd.BDD;
import jdd.bdd.Permutation;

import java.util.stream.Stream;

public class BDDVariableFactory {

	private final BDD bdd;

	public BDDVariableFactory() {
		this.bdd = new BDD(1000,100);
	}

	public BDDVariable newVariable() {
		return BDDVariable.newVariable(bdd);
	}

	public BDDVariable newCube(boolean[] setVariables) {
		return BDDVariable.newCube(bdd, setVariables);
	}

	public Permutation createPermutation(Stream<BDDVariable> fromSetVariables, Stream<BDDVariable> toSetVariables) {
		return bdd.createPermutation(variableIds(fromSetVariables), variableIds(toSetVariables));
	}

	private int[] variableIds(Stream<BDDVariable> variables) {
		return variables.mapToInt(BDDVariable::id).toArray();
	}
}

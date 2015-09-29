package io.github.theangrydev.opper.scanner.bdd;

import jdd.bdd.BDD;
import jdd.bdd.Permutation;

import java.util.stream.Stream;

public class BinaryDecisionDiagramFactory {

	private final BDD bdd;

	public BinaryDecisionDiagramFactory() {
		this.bdd = new BDD(1000,100);
	}

	public BinaryDecisionDiagram newVariable() {
		return BinaryDecisionDiagram.newVariable(bdd);
	}

	public BinaryDecisionDiagram newCube(boolean[] setVariables) {
		return BinaryDecisionDiagram.newCube(bdd, setVariables);
	}

	public Permutation createPermutation(Stream<BinaryDecisionDiagram> fromSetVariables, Stream<BinaryDecisionDiagram> toSetVariables) {
		return bdd.createPermutation(variableIds(fromSetVariables), variableIds(toSetVariables));
	}

	private int[] variableIds(Stream<BinaryDecisionDiagram> variables) {
		return variables.mapToInt(BinaryDecisionDiagram::id).toArray();
	}
}

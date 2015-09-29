package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramFactory;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;

import java.util.ArrayList;
import java.util.List;

public class AllVariables {

	private final List<BinaryDecisionDiagram> binaryDecisionDiagrams;
	private final List<BinaryDecisionDiagram> bddNotVariables;

	public AllVariables(int numberOfVariables, BinaryDecisionDiagramFactory binaryDecisionDiagramFactory) {
		binaryDecisionDiagrams = new ArrayList<>(numberOfVariables);
		for (int i = 0; i < numberOfVariables; i++) {
			binaryDecisionDiagrams.add(binaryDecisionDiagramFactory.newVariable());
		}

		bddNotVariables = new ArrayList<>(numberOfVariables);
		for (int i = 0; i < numberOfVariables; i++) {
			bddNotVariables.add(binaryDecisionDiagrams.get(i).not());
		}
	}

	public BinaryDecisionDiagram variable(VariableOrder variableOrder) {
		return variable(variableOrder.order());
	}

	public BinaryDecisionDiagram variable(int variableIndex) {
		return binaryDecisionDiagrams.get(variableIndex);
	}

	public BinaryDecisionDiagram notVariable(int variableIndex) {
		return bddNotVariables.get(variableIndex);
	}
}

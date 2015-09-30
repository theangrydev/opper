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

	public BinaryDecisionDiagram variable(Variable variable) {
		return variable(variable.order());
	}

	private BinaryDecisionDiagram variable(int variableIndex) {
		return binaryDecisionDiagrams.get(variableIndex);
	}

	private BinaryDecisionDiagram notVariable(int variableIndex) {
		return bddNotVariables.get(variableIndex);
	}

	public BinaryDecisionDiagram specifyVariables(List<Variable> variablesToSpecify, SetVariables setVariables) {
		BinaryDecisionDiagram specifiedVariables = specifyVariable(variablesToSpecify.get(0), setVariables);
		for (int i = 1; i < variablesToSpecify.size(); i++) {
			BinaryDecisionDiagram specifiedVariable = specifyVariable(variablesToSpecify.get(i), setVariables);
			specifiedVariables = specifiedVariables.andTo(specifiedVariable);
		}
		return specifiedVariables;
	}

	private BinaryDecisionDiagram specifyVariable(Variable variable, SetVariables setVariables) {
		if (setVariables.contains(variable)) {
			return variable(variable.order());
		} else {
			return notVariable(variable.order());
		}
	}
}

package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramFactory;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import jdd.bdd.Permutation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AllVariables {

	private final VariableOrdering variableOrdering;
	private final BinaryDecisionDiagramFactory binaryDecisionDiagramFactory;
	private final List<BinaryDecisionDiagram> binaryDecisionDiagrams;
	private final List<BinaryDecisionDiagram> bddNotVariables;

	public AllVariables(VariableOrdering variableOrdering) {
		this.variableOrdering = variableOrdering;
		this.binaryDecisionDiagramFactory = new BinaryDecisionDiagramFactory();

		int numberOfVariables = variableOrdering.numberOfVariables();
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

	private BinaryDecisionDiagram anything() {
		return binaryDecisionDiagramFactory.anything();
	}

	public BinaryDecisionDiagram specifyVariables(List<Variable> variablesToSpecify, VariablesSet variablesSet) {
		BinaryDecisionDiagram specifiedVariables = anything();
		for (Variable specified : variablesToSpecify) {
			BinaryDecisionDiagram specifiedVariable = specifyVariable(specified, variablesSet);
			specifiedVariables = specifiedVariables.andTo(specifiedVariable);
		}
		return specifiedVariables;
	}

	private BinaryDecisionDiagram specifyVariable(Variable variable, VariablesSet variablesSet) {
		if (variablesSet.contains(variable)) {
			return variable(variable.order());
		} else {
			return notVariable(variable.order());
		}
	}

	public Permutation createPermutation(Stream<BinaryDecisionDiagram> fromSetVariables, Stream<BinaryDecisionDiagram> toSetVariables) {
		return binaryDecisionDiagramFactory.createPermutation(fromSetVariables, toSetVariables);
	}

	private BinaryDecisionDiagram exists(List<Variable> presentVariables) {
		boolean[] variables = new boolean[variableOrdering.numberOfVariables()];
		for (Variable presentVariable : presentVariables) {
			variables[presentVariable.order()] = true;
		}
		return binaryDecisionDiagramFactory.newCube(variables);
	}

	public BinaryDecisionDiagram existsFromStateAndCharacter() {
		return exists(variableOrdering.fromStateOrCharacterVariables());
	}

	public BinaryDecisionDiagram nothing() {
		return binaryDecisionDiagramFactory.nothing();
	}

	public BinaryDecisionDiagram specifyAllVariables(VariablesSet variablesSet) {
		return specifyVariables(variableOrdering.allVariables(), variablesSet);
	}

	public BinaryDecisionDiagram specifyCharacterVariables(VariablesSet characterVariablesSet) {
		return specifyVariables(variableOrdering.characterVariables(), characterVariablesSet);
	}
}
package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramFactory;
import jdd.bdd.Permutation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AllVariables {

	private final VariableSummary variableSummary;
	private final VariableOrdering variableOrdering;
	private final BinaryDecisionDiagramFactory binaryDecisionDiagramFactory;
	private final List<BinaryDecisionDiagram> binaryDecisionDiagrams;
	private final List<BinaryDecisionDiagram> bddNotVariables;

	public AllVariables(VariableSummary variableSummary, VariableOrdering variableOrdering) {
		this.variableSummary = variableSummary;
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

	private BinaryDecisionDiagram variable(Variable variable) {
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

	private BinaryDecisionDiagram specifyVariablePresence(List<Variable> variablesToSpecify, VariablesSet variablesSet) {
		BinaryDecisionDiagram specifiedVariables = anything();
		for (Variable specified : variablesToSpecify) {
			BinaryDecisionDiagram specifiedVariable = specifyVariablePresence(specified, variablesSet);
			specifiedVariables = specifiedVariables.andTo(specifiedVariable);
		}
		return specifiedVariables;
	}

	private BinaryDecisionDiagram specifyVariablePresence(Variable variable, VariablesSet variablesSet) {
		if (variablesSet.contains(variable)) {
			return variable(variable.order());
		} else {
			return notVariable(variable.order());
		}
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
		return specifyVariablePresence(variableOrdering.allVariables(), variablesSet);
	}

	public BinaryDecisionDiagram specifyCharacterVariables(CharacterTransition characterTransition) {
		return specifyVariablePresence(variableOrdering.characterVariables(), variableSummary.variablesSetForCharacter(characterTransition));
	}

	public BinaryDecisionDiagram specifyToVariables(State toState) {
		return specifyVariablePresence(variableOrdering.toStateVariables(), variableSummary.variablesSetForToState(toState));
	}

	public BinaryDecisionDiagram specifyFromVariables(State fromState) {
		return specifyVariablePresence(variableOrdering.fromStateVariables(), variableSummary.variablesSetForFromState(fromState));
	}

	public Permutation relabelToStateToFromState() {
		Stream<BinaryDecisionDiagram> toVariables = variableOrdering.toStateVariablesInOriginalOrder().map(this::variable);
		Stream<BinaryDecisionDiagram> fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().map(this::variable);
		return binaryDecisionDiagramFactory.createPermutation(toVariables, fromVariables);
	}
}

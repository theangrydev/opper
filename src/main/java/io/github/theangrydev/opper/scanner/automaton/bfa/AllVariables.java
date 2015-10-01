package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramFactory;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramVariableAssignment;
import jdd.bdd.Permutation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AllVariables {

	private final VariableSummary variableSummary;
	private final VariableOrdering variableOrdering;
	private final BinaryDecisionDiagramFactory binaryDecisionDiagramFactory;
	private final List<BinaryDecisionDiagram> bddVariables;
	private final List<BinaryDecisionDiagram> bddNotVariables;

	private AllVariables(VariableSummary variableSummary, VariableOrdering variableOrdering, BinaryDecisionDiagramFactory binaryDecisionDiagramFactory, List<BinaryDecisionDiagram> bddVariables, List<BinaryDecisionDiagram> bddNotVariables) {
		this.variableSummary = variableSummary;
		this.variableOrdering = variableOrdering;
		this.binaryDecisionDiagramFactory = binaryDecisionDiagramFactory;
		this.bddVariables = bddVariables;
		this.bddNotVariables = bddNotVariables;
	}

	public static AllVariables allVariables(VariableSummary variableSummary, VariableOrdering variableOrdering) {
		BinaryDecisionDiagramFactory binaryDecisionDiagramFactory = new BinaryDecisionDiagramFactory();
		int numberOfVariables = variableOrdering.numberOfVariables();
		List<BinaryDecisionDiagram> bddVariables = bddVariables(binaryDecisionDiagramFactory, numberOfVariables);
		List<BinaryDecisionDiagram> bddNotVariables = bddNotVariables(numberOfVariables, bddVariables);
		return new AllVariables(variableSummary, variableOrdering, binaryDecisionDiagramFactory, bddVariables, bddNotVariables);
	}

	private static List<BinaryDecisionDiagram> bddNotVariables(int numberOfVariables, List<BinaryDecisionDiagram> bddVariables) {
		List<BinaryDecisionDiagram> bddNotVariables = new ArrayList<>(numberOfVariables);
		for (int i = 0; i < numberOfVariables; i++) {
			bddNotVariables.add(bddVariables.get(i).not());
		}
		return bddNotVariables;
	}

	private static List<BinaryDecisionDiagram> bddVariables(BinaryDecisionDiagramFactory binaryDecisionDiagramFactory, int numberOfVariables) {
		List<BinaryDecisionDiagram> bddVariables = new ArrayList<>(numberOfVariables);
		for (int i = 0; i < numberOfVariables; i++) {
			bddVariables.add(binaryDecisionDiagramFactory.newVariable());
		}
		return bddVariables;
	}

	private BinaryDecisionDiagram variable(Variable variable) {
		return variable(variable.order());
	}

	private BinaryDecisionDiagram variable(int variableIndex) {
		return bddVariables.get(variableIndex);
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

	public int toStateId(BinaryDecisionDiagramVariableAssignment toStateAssignment) {
		int[] assignment = toStateAssignment.assignment();
		int toStateId = 0;
		for (int i = assignment.length - 1; i >= 0; i--) {
			if (assignment[i] != 1) {
				continue;
			}
			int variableId = variableOrdering.variableId(i);
			int bitPosition = variableSummary.toStateBitPositionForVariableId(variableId);
			toStateId |= 1 << bitPosition;
		}
		return toStateId;
	}
}

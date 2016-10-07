/*
 * Copyright 2015 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.Transition;
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

	public BinaryDecisionDiagram anything() {
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

	public BinaryDecisionDiagram specifyCharacterVariables(Transition transition) {
		return specifyVariablePresence(variableOrdering.characterVariables(), variableSummary.variablesSetForTransition(transition));
	}

	public BinaryDecisionDiagram specifyFromVariables(State fromState) {
		return specifyVariablePresence(variableOrdering.fromStateVariables(), variableSummary.variablesSetForFromState(fromState));
	}

	public BinaryDecisionDiagram specifyFromVariables(List<State> fromStates) {
		BinaryDecisionDiagram fromVariables = nothing();
		for (State fromState : fromStates) {
			fromVariables = fromVariables.orTo(specifyFromVariables(fromState));
		}
		return fromVariables;
	}

	public Permutation relabelToStateToFromState() {
		Stream<BinaryDecisionDiagram> toVariables = variableOrdering.toStateVariablesInOriginalOrder().map(this::variable);
		Stream<BinaryDecisionDiagram> fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().map(this::variable);
		return binaryDecisionDiagramFactory.createPermutation(toVariables, fromVariables);
	}

	public int fromStateId(int[] fromStateAssignment) {
		int fromStateId = 0;
		for (int i = fromStateAssignment.length - 1; i >= 0; i--) {
			if (fromStateAssignment[i] != 1) {
				continue;
			}
			int variableId = variableOrdering.variableId(i);
			int bitPosition = variableSummary.fromStateBitPositionForVariableId(variableId);
			fromStateId |= 1 << bitPosition;
		}
		return fromStateId;
	}

	public int[] assignmentBuffer() {
		return new int[variableOrdering.numberOfVariables()];
	}
}

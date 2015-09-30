package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramVariableAssignment;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.Optional;

public class BFA {

	private final BFATransitions bfaTransitions;
	private final BinaryDecisionDiagram acceptingStates;
	private final BinaryDecisionDiagram initialState;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private final List<Symbol> symbolsByStateId;
	private final Permutation relabelToStateToFromState;

	public BFA(BFATransitions bfaTransitions, BinaryDecisionDiagram acceptingStates, BinaryDecisionDiagram initialState, VariableOrdering variableOrdering, VariableSummary variableSummary, List<Symbol> symbolsByStateId, Permutation relabelToStateToFromState) {
		this.bfaTransitions = bfaTransitions;
		this.acceptingStates = acceptingStates;
		this.initialState = initialState;
		this.variableOrdering = variableOrdering;
		this.variableSummary = variableSummary;
		this.symbolsByStateId = symbolsByStateId;
		this.relabelToStateToFromState = relabelToStateToFromState;
	}

	public BinaryDecisionDiagram relabelToStateToFromState(BinaryDecisionDiagram frontier) {
		return frontier.replaceTo(relabelToStateToFromState);
	}

	public BinaryDecisionDiagram initialState() {
		return initialState;
	}

	private Symbol symbolForAssignment(BinaryDecisionDiagramVariableAssignment assignment) {
		return symbolsByStateId.get(lookupToState(assignment));
	}

	private int lookupToState(BinaryDecisionDiagramVariableAssignment assignment) {
		return assignment.assignedVariableIndexes()
			.map(variableOrdering::variableId)
			.map(variableSummary::unprojectToIdBitPosition)
			.map(bitPosition -> 1 << bitPosition)
			.reduce(0, (a, b) -> a | b);
	}

	public Optional<Symbol> checkAcceptance(BinaryDecisionDiagram frontier) {
		BinaryDecisionDiagram acceptCheck = acceptingStates.and(frontier);
		boolean accepted = acceptCheck.isNotZero();
		if (!accepted) {
			acceptCheck.discard();
			return Optional.empty();
		}
		acceptCheck.discard();
		BinaryDecisionDiagramVariableAssignment satisfyingAssignment = acceptCheck.oneSatisfyingAssignment();
		Symbol acceptedSymbol = symbolForAssignment(satisfyingAssignment);
		return Optional.of(acceptedSymbol);
	}

	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		return bfaTransitions.transition(frontier, character);
	}
}

package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramVariableAssignment;

import java.util.List;
import java.util.Optional;

public class BFAAcceptance {
	private final BinaryDecisionDiagram acceptingStates;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private final List<Symbol> symbolsByStateId;

	public BFAAcceptance(BinaryDecisionDiagram acceptingStates, VariableOrdering variableOrdering, VariableSummary variableSummary, List<Symbol> symbolsByStateId) {
		this.acceptingStates = acceptingStates;
		this.variableOrdering = variableOrdering;
		this.variableSummary = variableSummary;
		this.symbolsByStateId = symbolsByStateId;
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
}

package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramVariableAssignment;

import java.util.List;
import java.util.Optional;

public class BFAAcceptance {
	private final BinaryDecisionDiagram acceptingStates;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private final List<Symbol> symbolsByStateId;

	private BFAAcceptance(BinaryDecisionDiagram acceptingStates, VariableOrdering variableOrdering, VariableSummary variableSummary, List<Symbol> symbolsByStateId) {
		this.acceptingStates = acceptingStates;
		this.variableOrdering = variableOrdering;
		this.variableSummary = variableSummary;
		this.symbolsByStateId = symbolsByStateId;
	}

	public static BFAAcceptance bfaAcceptance(NFA nfa, VariableOrdering variableOrdering, AllVariables allVariables) {
		VariableSummary variableSummary = nfa.variableSummary();
		BinaryDecisionDiagram acceptingStates = acceptingStates(variableOrdering, nfa, variableSummary, allVariables);
		List<Symbol> symbolsByStateId = nfa.symbolsByStateId();
		return new BFAAcceptance(acceptingStates, variableOrdering, variableSummary, symbolsByStateId);
	}

	private static BinaryDecisionDiagram acceptingStates(VariableOrdering variableOrdering, NFA nfa, VariableSummary variableSummary, AllVariables allVariables) {
		List<State> acceptanceStates = nfa.acceptanceStates();
		List<Variable> toStateVariables = variableOrdering.toStateVariables();

		SetVariables firstAcceptanceState = SetVariables.toState(variableSummary, acceptanceStates.get(0));
		BinaryDecisionDiagram acceptingStates = allVariables.specifyVariables(toStateVariables, firstAcceptanceState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			State state = acceptanceStates.get(i);
			BinaryDecisionDiagram acceptingState = allVariables.specifyVariables(toStateVariables, SetVariables.toState(variableSummary, state));
			acceptingStates = acceptingStates.orTo(acceptingState);
		}
		return acceptingStates;
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

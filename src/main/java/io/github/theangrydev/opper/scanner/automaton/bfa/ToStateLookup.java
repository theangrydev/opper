package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramVariableAssignment;

import java.util.List;

public class ToStateLookup {

	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private final List<Symbol> symbolsByStateId;

	private ToStateLookup(VariableOrdering variableOrdering, VariableSummary variableSummary, List<Symbol> symbolsByStateId) {
		this.variableOrdering = variableOrdering;
		this.variableSummary = variableSummary;
		this.symbolsByStateId = symbolsByStateId;
	}

	public static ToStateLookup make(NFA nfa, VariableOrdering variableOrdering) {
		List<Symbol> symbolsByStateId = nfa.symbolsByStateId();
		VariableSummary variableSummary = nfa.variableSummary();
		return new ToStateLookup(variableOrdering, variableSummary, symbolsByStateId);
	}

	public Symbol symbolForAssignment(BinaryDecisionDiagramVariableAssignment assignment) {
		return symbolsByStateId.get(lookupToState(assignment));
	}

	private int lookupToState(BinaryDecisionDiagramVariableAssignment assignment) {
		return assignment.assignedVariableIndexes()
			.map(variableOrdering::variableId)
			.map(variableSummary::toStateBitPositionForVariableId)
			.map(bitPosition -> 1 << bitPosition)
			.reduce(0, (a, b) -> a | b);
	}
}

package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;

import java.util.Optional;

public class BFAAcceptance {

	private final SymbolForAssignment symbolForAssignment;
	private final BinaryDecisionDiagram acceptingStates;
	private final BinaryDecisionDiagram acceptCheckBuffer;
	private final int[] toStateAssignmentBuffer;

	private BFAAcceptance(BinaryDecisionDiagram acceptCheckBuffer, BinaryDecisionDiagram acceptingStates, SymbolForAssignment symbolForAssignment, int[] toStateAssignmentBuffer) {
		this.acceptCheckBuffer = acceptCheckBuffer;
		this.acceptingStates = acceptingStates;
		this.symbolForAssignment = symbolForAssignment;
		this.toStateAssignmentBuffer = toStateAssignmentBuffer;
	}

	public static BFAAcceptance bfaAcceptance(NFA nfa, AllVariables allVariables) {
		BinaryDecisionDiagram acceptingStates = acceptingStates(nfa, allVariables);
		SymbolForAssignment symbolForAssignment = SymbolForAssignment.make(nfa, allVariables);
		return new BFAAcceptance(allVariables.anything(), acceptingStates, symbolForAssignment, allVariables.assignmentBuffer());
	}

	private static BinaryDecisionDiagram acceptingStates(NFA nfa, AllVariables allVariables) {
		BinaryDecisionDiagram acceptingStates = allVariables.nothing();
		for (State specifiedAcceptingState : nfa.acceptanceStates()) {
			BinaryDecisionDiagram acceptingState = allVariables.specifyFromVariables(specifiedAcceptingState);
			acceptingStates = acceptingStates.orTo(acceptingState);
		}
		return acceptingStates;
	}

	public Optional<Symbol> checkAcceptance(BinaryDecisionDiagram frontier) {
		BinaryDecisionDiagram acceptCheck = acceptingStates.and(frontier, acceptCheckBuffer);
		if (acceptCheck.isZero()) {
			return Optional.empty();
		}
		int[] toStateAssignment = acceptCheck.oneSatisfyingAssignment(toStateAssignmentBuffer);
		Symbol acceptedSymbol = symbolForAssignment.symbolForToState(toStateAssignment);
		return Optional.of(acceptedSymbol);
	}

}

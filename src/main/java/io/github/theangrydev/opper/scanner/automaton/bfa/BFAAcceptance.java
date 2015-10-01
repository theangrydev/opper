package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramVariableAssignment;

import java.util.List;
import java.util.Optional;

public class BFAAcceptance {

	private final SymbolForAssignment symbolForAssignment;
	private final BinaryDecisionDiagram acceptingStates;

	private BFAAcceptance(BinaryDecisionDiagram acceptingStates, SymbolForAssignment symbolForAssignment) {
		this.acceptingStates = acceptingStates;
		this.symbolForAssignment = symbolForAssignment;
	}

	public static BFAAcceptance bfaAcceptance(NFA nfa, AllVariables allVariables) {
		BinaryDecisionDiagram acceptingStates = acceptingStates(nfa, allVariables);
		SymbolForAssignment symbolForAssignment = SymbolForAssignment.make(nfa, allVariables);
		return new BFAAcceptance(acceptingStates, symbolForAssignment);
	}

	private static BinaryDecisionDiagram acceptingStates(NFA nfa, AllVariables allVariables) {
		List<State> specifiedAcceptingStates = nfa.acceptanceStates();
		BinaryDecisionDiagram acceptingStates = allVariables.nothing();
		for (State specifiedAcceptingState : specifiedAcceptingStates) {
			BinaryDecisionDiagram acceptingState = allVariables.specifyToVariables(specifiedAcceptingState);
			acceptingStates = acceptingStates.orTo(acceptingState);
		}
		return acceptingStates;
	}

	public Optional<Symbol> checkAcceptance(BinaryDecisionDiagram frontier) {
		BinaryDecisionDiagram acceptCheck = acceptingStates.and(frontier);
		acceptCheck.discard();
		boolean accepted = acceptCheck.isNotZero();
		if (!accepted) {
			return Optional.empty();
		}
		BinaryDecisionDiagramVariableAssignment satisfyingAssignment = acceptCheck.oneSatisfyingAssignment();
		Symbol acceptedSymbol = symbolForAssignment.symbolForToState(satisfyingAssignment);
		return Optional.of(acceptedSymbol);
	}

}

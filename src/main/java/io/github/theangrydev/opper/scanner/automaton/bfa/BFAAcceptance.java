package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramVariableAssignment;

import java.util.List;
import java.util.Optional;

public class BFAAcceptance {

	private final ToStateLookup toStateLookup;
	private final BinaryDecisionDiagram acceptingStates;

	private BFAAcceptance(BinaryDecisionDiagram acceptingStates, ToStateLookup toStateLookup) {
		this.acceptingStates = acceptingStates;
		this.toStateLookup = toStateLookup;
	}

	public static BFAAcceptance bfaAcceptance(NFA nfa, VariableOrdering variableOrdering, AllVariables allVariables) {
		VariableSummary variableSummary = nfa.variableSummary();
		BinaryDecisionDiagram acceptingStates = acceptingStates(variableOrdering, nfa, variableSummary, allVariables);
		ToStateLookup toStateLookup = ToStateLookup.make(nfa, variableOrdering);
		return new BFAAcceptance(acceptingStates, toStateLookup);
	}

	private static BinaryDecisionDiagram acceptingStates(VariableOrdering variableOrdering, NFA nfa, VariableSummary variableSummary, AllVariables allVariables) {
		List<State> specifiedAcceptingStates = nfa.acceptanceStates();
		List<Variable> toStateVariables = variableOrdering.toStateVariables();

		BinaryDecisionDiagram acceptingStates = allVariables.nothing();
		for (State specifiedAcceptingState : specifiedAcceptingStates) {
			BinaryDecisionDiagram acceptingState = allVariables.specifyVariables(toStateVariables, variableSummary.variablesSetForToState(specifiedAcceptingState));
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
		Symbol acceptedSymbol = toStateLookup.symbolForAssignment(satisfyingAssignment);
		return Optional.of(acceptedSymbol);
	}

}

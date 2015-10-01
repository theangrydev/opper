package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.stream.Stream;

public class BFABuilder {

	public static BFA convertToBFA(NFA nfa) {
		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);
		VariableOrdering variableOrdering = VariableOrderingComputer.determineOrdering(nfa.variableSummary(), transitionTable);

		AllVariables allVariables = new AllVariables(variableOrdering);

		BFAAcceptance bfaAcceptance = BFAAcceptance.bfaAcceptance(nfa, variableOrdering, allVariables);

		BFATransitions bfaTransitions = BFATransitions.bfaTransitions(nfa, transitionTable, allVariables);

		BinaryDecisionDiagram startingFrom = initialState(nfa, variableOrdering, allVariables);
		Permutation relabelToStateToFromState = relabelToStateToFromState(variableOrdering, allVariables);

		return new BFA(bfaTransitions, bfaAcceptance, startingFrom, relabelToStateToFromState);
	}

	private static BinaryDecisionDiagram initialState(NFA nfa, VariableOrdering variableOrdering, AllVariables allVariables) {
		List<Variable> fromStateVariables = variableOrdering.fromStateVariables();
		VariablesSet fromState = nfa.variableSummary().variablesSetForFromState(nfa.initialState());
		return allVariables.specifyVariables(fromStateVariables, fromState);
	}

	private static Permutation relabelToStateToFromState(VariableOrdering variableOrdering, AllVariables allVariables) {
		Stream<BinaryDecisionDiagram> toVariables = variableOrdering.toStateVariablesInOriginalOrder().map(allVariables::variable);
		Stream<BinaryDecisionDiagram> fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().map(allVariables::variable);
		return allVariables.createPermutation(toVariables, fromVariables);
	}
}

package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import jdd.bdd.Permutation;

public class BFABuilder {

	public static BFA convertToBFA(NFA nfa) {
		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);
		VariableOrdering variableOrdering = VariableOrderingComputer.determineOrdering(nfa.variableSummary(), transitionTable);

		AllVariables allVariables = new AllVariables(variableOrdering);

		BFAAcceptance bfaAcceptance = BFAAcceptance.bfaAcceptance(nfa, variableOrdering, allVariables);

		BFATransitions bfaTransitions = BFATransitions.bfaTransitions(nfa, transitionTable, allVariables);

		BinaryDecisionDiagram startingFrom = initialState(nfa, allVariables);

		Permutation relabelToStateToFromState = allVariables.relabelToStateToFromState();

		return new BFA(bfaTransitions, bfaAcceptance, startingFrom, relabelToStateToFromState);
	}

	private static BinaryDecisionDiagram initialState(NFA nfa, AllVariables allVariables) {
		VariablesSet fromState = nfa.variableSummary().variablesSetForFromState(nfa.initialState());
		return allVariables.specifyFromVariables(fromState);
	}
}

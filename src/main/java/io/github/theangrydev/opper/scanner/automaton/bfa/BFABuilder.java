package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;

public class BFABuilder {

	public static BFA convertToBFA(NFA nfa) {
		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);
		VariableOrdering variableOrdering = VariableOrderingComputer.determineOrdering(nfa.variableSummary(), transitionTable);

		AllVariables allVariables = AllVariables.allVariables(nfa.variableSummary(), variableOrdering);

		BFAAcceptance bfaAcceptance = BFAAcceptance.bfaAcceptance(nfa, allVariables);

		BFATransitions bfaTransitions = BFATransitions.bfaTransitions(nfa, transitionTable, allVariables);

		BinaryDecisionDiagram startingFrom = allVariables.specifyFromVariables(nfa.initialStates());

		return new BFA(bfaTransitions, bfaAcceptance, startingFrom);
	}
}

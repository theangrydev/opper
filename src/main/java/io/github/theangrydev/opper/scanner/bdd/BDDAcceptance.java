package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.SetVariables;
import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.VariableOrdering;
import jdd.bdd.BDD;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class BDDAcceptance {

	public int compute(VariableOrdering variableOrdering, List<State> states, VariableSummary variableSummary, BDD bdd, BDDVariables bddVariables) {
		List<VariableOrder> toStateVariableOrders = variableOrdering.toStateVariables().collect(toList());
		List<State> acceptanceStates = states.stream().filter(State::isAccepting).collect(toList());

		SetVariables firstToState = SetVariables.toState(variableSummary, acceptanceStates.get(0));
		int bddDisjunction = BDDRowComputer.bddRow(toStateVariableOrders, bdd, bddVariables, firstToState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			State state = acceptanceStates.get(i);
			SetVariables toState = SetVariables.toState(variableSummary, state);
			int bddRow = BDDRowComputer.bddRow(toStateVariableOrders, bdd, bddVariables, toState);
			bddDisjunction = bdd.orTo(bddDisjunction, bddRow);
		}
		return bddDisjunction;
	}
}

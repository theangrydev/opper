package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.SetVariables;
import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.VariableOrdering;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class BDDAcceptance {

	public BDDVariable compute(VariableOrdering variableOrdering, List<State> states, VariableSummary variableSummary, BDDVariables bddVariables) {
		List<VariableOrder> toStateVariableOrders = variableOrdering.toStateVariables().collect(toList());
		List<State> acceptanceStates = states.stream().filter(State::isAccepting).collect(toList());

		SetVariables firstToState = SetVariables.toState(variableSummary, acceptanceStates.get(0));
		BDDVariable bddDisjunction = BDDRowComputer.bddRow(toStateVariableOrders, bddVariables, firstToState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			State state = acceptanceStates.get(i);
			SetVariables toState = SetVariables.toState(variableSummary, state);
			BDDVariable bddRow = BDDRowComputer.bddRow(toStateVariableOrders, bddVariables, toState);
			bddDisjunction = bddDisjunction.orTo(bddRow);
		}
		return bddDisjunction;
	}
}

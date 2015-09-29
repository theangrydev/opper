package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.SetVariables;
import io.github.theangrydev.opper.scanner.autonoma.TransitionTable;
import io.github.theangrydev.opper.scanner.autonoma.VariableOrdering;

import java.util.List;

public class BDDTransitionsTableComputer {
	public BDDVariable compute(VariableOrdering variableOrders, BDDVariables bddVariables, TransitionTable transitionTable) {
		List<SetVariables> transitions = transitionTable.transitions();
		BDDVariable bddDisjunction = BDDRowComputer.bddRow(variableOrders.allVariables(), bddVariables, transitions.get(0));
		for (int i = 1; i < transitions.size(); i++) {
			BDDVariable bddRow = BDDRowComputer.bddRow(variableOrders.allVariables(), bddVariables, transitions.get(i));
			bddDisjunction = bddDisjunction.orTo(bddRow);
		}
		return bddDisjunction;
	}

}

package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.TransitionTable;
import io.github.theangrydev.opper.scanner.autonoma.SetVariables;
import io.github.theangrydev.opper.scanner.autonoma.VariableOrdering;
import jdd.bdd.BDD;

import java.util.List;

public class BDDTransitionsTableComputer {
	public int compute(VariableOrdering variableOrders, BDD bdd, BDDVariables bddVariables, TransitionTable transitionTable) {
		List<SetVariables> transitions = transitionTable.transitions();
		int bddDisjunction = BDDRowComputer.bddRow(variableOrders.all(), bdd, bddVariables, transitions.get(0));
		for (int i = 1; i < transitions.size(); i++) {
			int bddRow = BDDRowComputer.bddRow(variableOrders.all(), bdd, bddVariables, transitions.get(i));
			bddDisjunction = bdd.orTo(bddDisjunction, bddRow);
		}
		return bddDisjunction;
	}

}

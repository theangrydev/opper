package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.SetVariables;
import jdd.bdd.BDD;

import java.util.List;

public class BDDRowComputer {
	public static int bddRow(List<VariableOrder> variableOrders, BDD bdd, BDDVariables bddVariables, SetVariables setVariables) {
		int bddRow = setVariable(bddVariables, setVariables, variableOrders.get(0));
		for (int i = 1; i < variableOrders.size(); i++) {
			int bddVariable = setVariable(bddVariables, setVariables, variableOrders.get(i));
			bddRow = bdd.andTo(bddRow, bddVariable);
		}
		return bddRow;
	}

	public static int setVariable(BDDVariables bddVariables, SetVariables setVariables, VariableOrder variableOrder) {
		if (setVariables.contains(variableOrder)) {
			return bddVariables.variable(variableOrder.order());
		} else {
			return bddVariables.notVariable(variableOrder.order());
		}
	}
}

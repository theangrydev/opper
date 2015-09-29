package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.SetVariables;

import java.util.List;

public class BDDRowComputer {
	public static BDDVariable bddRow(List<VariableOrder> variableOrders, BDDVariables bddVariables, SetVariables setVariables) {
		BDDVariable bddRow = setVariable(bddVariables, setVariables, variableOrders.get(0));
		for (int i = 1; i < variableOrders.size(); i++) {
			BDDVariable bddVariable = setVariable(bddVariables, setVariables, variableOrders.get(i));
			bddRow = bddRow.andTo(bddVariable);
		}
		return bddRow;
	}

	public static BDDVariable setVariable(BDDVariables bddVariables, SetVariables setVariables, VariableOrder variableOrder) {
		if (setVariables.contains(variableOrder)) {
			return bddVariables.variable(variableOrder.order());
		} else {
			return bddVariables.notVariable(variableOrder.order());
		}
	}
}

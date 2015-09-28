package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.SetVariables;
import jdd.bdd.BDD;

import java.util.List;

public class BDDRowComputer {
	public static int bddRow(List<Variable> variables, BDD bdd, BDDVariables bddVariables, SetVariables setVariables) {
		int bddRow = setVariable(bddVariables, setVariables, variables.get(0));
		for (int i = 1; i < variables.size(); i++) {
			int bddVariable = setVariable(bddVariables, setVariables, variables.get(i));
			bddRow = bdd.andTo(bddRow, bddVariable);
		}
		return bddRow;
	}

	public static int setVariable(BDDVariables bddVariables, SetVariables setVariables, Variable variable) {
		if (setVariables.contains(variable)) {
			return bddVariables.variable(variable.order());
		} else {
			return bddVariables.notVariable(variable.order());
		}
	}
}

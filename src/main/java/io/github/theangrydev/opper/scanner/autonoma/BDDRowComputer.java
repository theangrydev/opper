package io.github.theangrydev.opper.scanner.autonoma;

import jdd.bdd.BDD;

import java.util.BitSet;
import java.util.List;

public class BDDRowComputer {
	public static int bddRow(List<Variable> variables, BDD bdd, BDDVariables bddVariables, BitSet row) {
		int bddRow = setVariable(variables, bddVariables, row, 0);
		for (int i = 1; i < variables.size(); i++) {
			int bddVariable = setVariable(variables, bddVariables, row, i);
			bddRow = bdd.andTo(bddRow, bddVariable);
		}
		return bddRow;
	}

	public static int setVariable(List<Variable> variables, BDDVariables bddVariables, BitSet row, int variableIndex) {
		Variable variable = variables.get(variableIndex);
		int bitIndex = variable.id() - 1;
		if (row.get(bitIndex)) {
			return bddVariables.variable(variable.order());
		} else {
			return bddVariables.notVariable(variable.order());
		}
	}
}

package io.github.theangrydev.opper.scanner.autonoma;

import it.unimi.dsi.fastutil.ints.IntList;
import jdd.bdd.BDD;

import java.util.BitSet;

public class BDDRowComputer {
	public static int bddRow(IntList variables, BDD bdd, BDDVariables bddVariables, BitSet row) {
		int bddRow = setVariable(variables, bddVariables, row, 0);
		for (int i = 1; i < variables.size(); i++) {
			int bddVariable = setVariable(variables, bddVariables, row, i);
			bddRow = bdd.andTo(bddRow, bddVariable);
		}
		return bddRow;
	}

	public static  int setVariable(IntList variables, BDDVariables bddVariables, BitSet row, int variableIndex) {
		int bitIndex = variables.getInt(variableIndex) - 1;
		if (row.get(bitIndex)) {
			return bddVariables.variable(variableIndex);
		} else {
			return bddVariables.notVariable(variableIndex);
		}
	}
}

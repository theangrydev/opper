package io.github.theangrydev.opper.scanner.autonoma;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import jdd.bdd.BDD;

import java.util.BitSet;
import java.util.List;

public class BDDTransitionsTable {
	public void compute(IntList variables, List<BitSet> transitionTable) {
		BDD bdd = new BDD(1000,100);

		System.out.println("variable order=" + variables);
		IntList bddVariables = new IntArrayList(variables.size());
		for (int i = 0; i < variables.size(); i++) {
			bddVariables.add(bdd.createVar());
		}
		System.out.println("bddVariables=" + bddVariables);

		IntList bddNotVariables = new IntArrayList(variables.size());
		for (int i = 0; i < variables.size(); i++) {
			bddNotVariables.add(bdd.not(bddVariables.getInt(i)));
		}
		System.out.println("bddNotVariables=" + bddNotVariables);

		int bddDisjunction = bddRow(variables, bdd, bddVariables, bddNotVariables, transitionTable.get(0));
		for (int i = 1; i < transitionTable.size(); i++) {
			int bddRow = bddRow(variables, bdd, bddVariables, bddNotVariables, transitionTable.get(i));
			bddDisjunction = bdd.orTo(bddDisjunction, bddRow);
		}
		bdd.printDot("test", bddDisjunction);
	}

	private int bddRow(IntList variables, BDD bdd, IntList bddVariables, IntList bddNotVariables, BitSet row) {
		int bddRow = setVariable(variables, bddVariables, bddNotVariables, row, 0);
		for (int i = 1; i < variables.size(); i++) {
			int bddVariable = setVariable(variables, bddVariables, bddNotVariables, row, i);
			bddRow = bdd.andTo(bddRow, bddVariable);
		}
		return bddRow;
	}

	private int setVariable(IntList variables, IntList bddVariables, IntList bddNotVariables, BitSet row, int variableIndex) {
		int bitIndex = variables.getInt(variableIndex) - 1;
		if (row.get(bitIndex)) {
			return bddVariables.getInt(variableIndex);
		} else {
			return bddNotVariables.getInt(variableIndex);
		}
	}
}

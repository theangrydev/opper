package io.github.theangrydev.opper.scanner.autonoma;

import jdd.bdd.BDD;

import java.util.BitSet;
import java.util.List;

public class BDDTransitionsTableComputer {
	public int compute(List<Variable> variables, BDD bdd, BDDVariables bddVariables, List<BitSet> transitionTable) {
		int bddDisjunction = BDDRowComputer.bddRow(variables, bdd, bddVariables, transitionTable.get(0));
		for (int i = 1; i < transitionTable.size(); i++) {
			int bddRow = BDDRowComputer.bddRow(variables, bdd, bddVariables, transitionTable.get(i));
			bddDisjunction = bdd.orTo(bddDisjunction, bddRow);
		}
		return bddDisjunction;
	}

}

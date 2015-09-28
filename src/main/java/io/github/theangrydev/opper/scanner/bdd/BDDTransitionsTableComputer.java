package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.TransitionTable;
import io.github.theangrydev.opper.scanner.autonoma.SetVariables;
import jdd.bdd.BDD;

import java.util.List;

public class BDDTransitionsTableComputer {
	public int compute(List<Variable> variables, BDD bdd, BDDVariables bddVariables, TransitionTable transitionTable) {
		List<SetVariables> transitions = transitionTable.transitions();
		int bddDisjunction = BDDRowComputer.bddRow(variables, bdd, bddVariables, transitions.get(0));
		for (int i = 1; i < transitions.size(); i++) {
			int bddRow = BDDRowComputer.bddRow(variables, bdd, bddVariables, transitions.get(i));
			bddDisjunction = bdd.orTo(bddDisjunction, bddRow);
		}
		return bddDisjunction;
	}

}

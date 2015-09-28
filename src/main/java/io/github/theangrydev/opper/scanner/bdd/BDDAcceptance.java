package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.State;
import jdd.bdd.BDD;

import java.util.BitSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class BDDAcceptance {

	public int compute(List<Variable> variables, List<State> states, BitSummary bitSummary, BDD bdd, BDDVariables bddVariables) {
		List<Variable> toStateVariables = variables.stream().filter(bitSummary::isToState).collect(toList());
		List<State> acceptanceStates = states.stream().filter(State::isAccepting).collect(toList());

		BitSet firstToState = BitSet.valueOf(new long[]{bitSummary.projectToId(acceptanceStates.get(0))});
		int bddDisjunction = BDDRowComputer.bddRow(toStateVariables, bdd, bddVariables, firstToState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			State state = acceptanceStates.get(i);
			BitSet toState = BitSet.valueOf(new long[]{bitSummary.projectToId(state)});
			int bddRow = BDDRowComputer.bddRow(toStateVariables, bdd, bddVariables, toState);
			bddDisjunction = bdd.orTo(bddDisjunction, bddRow);
		}
		return bddDisjunction;
	}
}

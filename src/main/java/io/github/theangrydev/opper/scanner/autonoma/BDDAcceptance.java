package io.github.theangrydev.opper.scanner.autonoma;

import jdd.bdd.BDD;

import java.util.BitSet;
import java.util.List;

import static io.github.theangrydev.opper.scanner.autonoma.BDDRowComputer.bddRow;
import static java.util.stream.Collectors.toList;

public class BDDAcceptance {

	public int compute(List<Variable> variables, List<State> states, BitSummary bitSummary, BDD bdd, BDDVariables bddVariables) {
		List<Variable> toStateVariables = variables.stream().filter(bitSummary::isToState).collect(toList());
		List<State> acceptanceStates = states.stream().filter(State::isAccepting).collect(toList());

		BitSet firstToState = BitSet.valueOf(new long[]{bitSummary.projectToId(acceptanceStates.get(0).id())});
		int bddDisjunction = BDDRowComputer.bddRow(toStateVariables, bdd, bddVariables, firstToState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			int stateId = acceptanceStates.get(i).id();
			BitSet toState = BitSet.valueOf(new long[]{bitSummary.projectToId(stateId)});
			int bddRow = bddRow(toStateVariables, bdd, bddVariables, toState);
			bddDisjunction = bdd.orTo(bddDisjunction, bddRow);
		}
		return bddDisjunction;
	}
}

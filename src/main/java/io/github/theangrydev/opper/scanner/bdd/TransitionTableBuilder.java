package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.State;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TransitionTableBuilder {

	public List<BitSet> buildTransitionTable(BitSummary bitSummary, List<State> states) {
		List<BitSet> transitions = new ArrayList<>();
		for (State state : states) {
			state.visitTransitions((from, via, to) -> {
				BitSet row = new BitSet(bitSummary.bitsPerRow());
				blastBits(bitSummary.projectFromId(from.id()), row);
				blastBits(bitSummary.projectCharacterId(via.id()), row);
				blastBits(bitSummary.projectToId(to.id()), row);
				transitions.add(row);
			});
		}

		return transitions;
	}

	private void blastBits(long number, BitSet row) {
		row.or(BitSet.valueOf(new long[]{number}));
	}
}

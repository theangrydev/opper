package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.scanner.bdd.BitSummary;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TransitionTable {
	private final List<BitSet> transitions;

	public TransitionTable(List<State> states, BitSummary bitSummary) {
		transitions = new ArrayList<>();
		for (State state : states) {
			state.visitTransitions((from, via, to) -> {
				BitSet row = new BitSet(bitSummary.bitsPerRow());
				blastBits(bitSummary.projectFromId(from), row);
				blastBits(bitSummary.projectCharacterId(via), row);
				blastBits(bitSummary.projectToId(to), row);
				transitions.add(row);
			});
		}
	}

	private void blastBits(long number, BitSet row) {
		row.or(BitSet.valueOf(new long[]{number}));
	}

	public List<BitSet> transitions() {
		return transitions;
	}
}

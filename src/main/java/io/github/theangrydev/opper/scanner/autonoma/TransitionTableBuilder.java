package io.github.theangrydev.opper.scanner.autonoma;

import it.unimi.dsi.fastutil.chars.Char2IntMap;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TransitionTableBuilder {

	public List<BitSet> buildTransitionTable(BitSummary bitSummary, Char2IntMap characterIds, List<State> states) {
		List<BitSet> transitions = new ArrayList<>();
		for (State state : states) {
			state.visitTransitions((from, via, to) -> {
				BitSet row = new BitSet(bitSummary.bitsPerRow());
				blastBits(bitSummary.projectFromId(from.id()), row);
				blastBits(bitSummary.projectCharacterId(characterIds.get(via)), row);
				blastBits(bitSummary.projectToId(to.id()), row);
				transitions.add(row);
			});
		}

		return transitions;
	}

	//TODO: this should result in a transition table encoded with rows as a BitSet with from, to, via
	//TODO: next step is to implement ID3 to determine which order the bits should be added to the ROBDD to achieve a minimal size
	//TODO: this will give a permutation of the current order
	//TODO: the permutation of the current order can be used to compute a new transition table, acceptance table, etc
	//TODO: at this point we are ready to convert all the bitsets to ROBDDs and implement the NFA-OBDD algorithm
	private void blastBits(long number, BitSet row) {
		row.or(BitSet.valueOf(new long[]{number}));
	}
}

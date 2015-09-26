package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.chars.Char2IntMap;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TransitionTableBuilder {

	public List<BitSet> buildTransitionTable(Char2IntMap characterIds, List<State> states) {
		int numberOfStates = states.size();
		int numberOfCharacters = characterIds.size();

		int bitsForStates = IntMath.log2(numberOfStates, RoundingMode.UP);
		int bitsForCharacters = IntMath.log2(numberOfCharacters, RoundingMode.UP);
		int bitsPerRow = bitsForStates * 2 + bitsForCharacters;

		List<BitSet> transitions = new ArrayList<>();
		BitSet row = new BitSet(bitsPerRow);
		for (State state : states) {
			state.visitTransitions((from, via, to) -> {
				blastBits(from.id(), row);
				blastBits(to.id() << bitsForStates, row);
				blastBits(characterIds.get(via) << bitsForStates << 2, row);
			});
		}

		return transitions;
	}

	//TODO: this should result in a transition table encoded with rows as a BitSet with from, to, via
	//TODO: next step is to implement ID3 to determine which order the bits should be added to the ROBDD to achieve a minimal size
	//TODO: this will give a permutation of the current order
	//TODO: the permutation of the current order can be used to compute a new transition table, acceptance table, etc
	//TODO: at this point we are ready to convert all the bitsets to ROBDDs and implement the NFA-OBDD algorithm
	private void blastBits(int number, BitSet row) {
		row.and(BitSet.valueOf(new long[] {number}));
	}
}

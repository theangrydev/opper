package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset.Entry;

public class StateEncoder {

	public void labelStatesWithSmallerIdsForMoreFrequentStates(ImmutableMultiset<State> stateFrequencies) {
		int idSequence = 1;
		for (Entry<State> entry : stateFrequencies.entrySet()) {
			entry.getElement().label(idSequence++);
		}
	}
}

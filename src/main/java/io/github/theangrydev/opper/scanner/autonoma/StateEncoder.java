package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.Multiset.Entry;

public class StateEncoder {

	public void labelStatesWithSmallerIdsForMoreFrequentStates(StateStatistics stateStatistics) {
		int idSequence = 1;
		for (Entry<State> entry : stateStatistics.stateFrequencies().entrySet()) {
			entry.getElement().label(idSequence++);
		}
	}
}

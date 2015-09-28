package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.Multiset;

public class CharacterEncoder {

	public void labelCharactersWithSmallerIdsForMoreFrequentCharacters(StateStatistics stateStatistics) {
		int idSequence = 1;
		for (Multiset.Entry<Transition> entry : stateStatistics.transitionFrequencies().entrySet()) {
			entry.getElement().label(idSequence++);
		}
	}
}

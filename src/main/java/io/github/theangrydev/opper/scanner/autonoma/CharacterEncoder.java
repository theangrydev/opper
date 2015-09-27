package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;

public class CharacterEncoder {

	public Char2IntMap labelCharactersWithSmallerIdsForMoreFrequentCharacters(StateStatistics stateStatistics) {
		Char2IntMap characterToId = new Char2IntArrayMap();
		int idSequence = 1;
		for (Multiset.Entry<Character> entry : stateStatistics.characterFrequencies().entrySet()) {
			characterToId.put((char) entry.getElement(), idSequence++);
		}
		return characterToId;
	}
}

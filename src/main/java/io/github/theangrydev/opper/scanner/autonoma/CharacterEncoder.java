package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;

public class CharacterEncoder {

	public Char2IntMap labelCharactersWithSmallerIdsForMoreFrequentCharacters(ImmutableMultiset<Character> characterFrequencies) {
		Char2IntMap characterToId = new Char2IntArrayMap();
		int idSequence = 0;
		for (Multiset.Entry<Character> entry : characterFrequencies.entrySet()) {
			characterToId.put((char) entry.getElement(), idSequence++);
		}
		return characterToId;
	}
}

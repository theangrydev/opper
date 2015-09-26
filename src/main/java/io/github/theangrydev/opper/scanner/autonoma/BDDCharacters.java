package io.github.theangrydev.opper.scanner.autonoma;

import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.ints.IntList;
import jdd.bdd.BDD;

import java.util.BitSet;

import static io.github.theangrydev.opper.scanner.autonoma.BDDRowComputer.bddRow;

public class BDDCharacters {

	public Char2IntMap compute(IntList variables, Char2IntMap characterIds, BitSummary bitSummary, BDD bdd, BDDVariables bddVariables) {
		Char2IntMap characterBddSets = new Char2IntArrayMap(characterIds.size());
		for (Char2IntMap.Entry entry : characterIds.char2IntEntrySet()) {
			System.out.println("char=" + entry.getCharKey());
			int characterId = characterIds.get(entry.getCharKey());
			BitSet character = BitSet.valueOf(new long[]{bitSummary.projectCharacterId(characterId)});
			int row = bddRow(variables, bdd, bddVariables, character);
			characterBddSets.put(entry.getCharKey(), row);
			bdd.printSet(row);
		}
		return characterBddSets;
	}
}

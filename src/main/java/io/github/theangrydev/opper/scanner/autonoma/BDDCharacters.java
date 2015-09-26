package io.github.theangrydev.opper.scanner.autonoma;

import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import jdd.bdd.BDD;

import java.util.BitSet;
import java.util.List;

import static io.github.theangrydev.opper.scanner.autonoma.BDDRowComputer.bddRow;
import static java.util.stream.Collectors.toList;

public class BDDCharacters {

	public Char2IntMap compute(List<Variable> variables, Char2IntMap characterIds, BitSummary bitSummary, BDD bdd, BDDVariables bddVariables) {
		List<Variable> characterVariables = variables.stream().filter(bitSummary::isCharacter).collect(toList());
		Char2IntMap characterBddSets = new Char2IntArrayMap(characterIds.size());
		for (Char2IntMap.Entry entry : characterIds.char2IntEntrySet()) {
			int characterId = characterIds.get(entry.getCharKey());
			BitSet character = BitSet.valueOf(new long[]{bitSummary.projectCharacterId(characterId)});
			int bddRow = bddRow(characterVariables, bdd, bddVariables, character);
			characterBddSets.put(entry.getCharKey(), bddRow);
		}
		return characterBddSets;
	}
}

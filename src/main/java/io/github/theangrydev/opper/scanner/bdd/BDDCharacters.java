package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.CharacterTransition;
import io.github.theangrydev.opper.scanner.autonoma.SetVariables;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import jdd.bdd.BDD;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class BDDCharacters {

	public Char2IntMap compute(List<Variable> variables, List<CharacterTransition> characterTransitions, VariableSummary variableSummary, BDD bdd, BDDVariables bddVariables) {
		List<Variable> characterVariables = variables.stream().filter(variableSummary::isCharacter).collect(toList());
		Char2IntMap characterBddSets = new Char2IntArrayMap(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			SetVariables character = SetVariables.character(variableSummary, characterTransition);
			int bddRow = BDDRowComputer.bddRow(characterVariables, bdd, bddVariables, character);
			characterBddSets.put(characterTransition.character(), bddRow);
		}
		return characterBddSets;
	}
}

/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterClassTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.Collection;

public class BFATransitions {
	private final BinaryDecisionDiagram transitions;
	private final Collection<CharacterClassTransition> characterClassTransitions;
	private final Char2ObjectMap<BinaryDecisionDiagram> characterPresences;
	private final BinaryDecisionDiagram existsFromStateAndCharacter;
	private final Permutation relabelToStateToFromState;
	private final AllVariables allVariables;

	private BFATransitions(BinaryDecisionDiagram transitions, Collection<CharacterClassTransition> characterClassTransitions, Char2ObjectMap<BinaryDecisionDiagram> characterPresences, BinaryDecisionDiagram existsFromStateAndCharacter, Permutation relabelToStateToFromState, AllVariables allVariables) {
		this.transitions = transitions;
		this.characterClassTransitions = characterClassTransitions;
		this.characterPresences = characterPresences;
		this.existsFromStateAndCharacter = existsFromStateAndCharacter;
		this.relabelToStateToFromState = relabelToStateToFromState;
		this.allVariables = allVariables;
	}

	public static BFATransitions bfaTransitions(NFA nfa, TransitionTable transitionTable, AllVariables allVariables) {
		Collection<CharacterClassTransition> characterClassTransitions = nfa.characterClassTransitions();
		BinaryDecisionDiagram transitions = transitions(allVariables, transitionTable);
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = characterPresence(nfa.characterTransitions(), characterClassTransitions, allVariables);
		BinaryDecisionDiagram existsFromStateAndCharacter = allVariables.existsFromStateAndCharacter();
		Permutation relabelToStateToFromState = allVariables.relabelToStateToFromState();
		return new BFATransitions(transitions, characterClassTransitions, characterPresences, existsFromStateAndCharacter, relabelToStateToFromState, allVariables);
	}

	private static BinaryDecisionDiagram transitions(AllVariables allVariables, TransitionTable transitionTable) {
		BinaryDecisionDiagram transitions = allVariables.nothing();
		for (VariablesSet transition : transitionTable.transitions()) {
			transitions = transitions.orTo(allVariables.specifyAllVariables(transition));
		}
		return transitions;
	}

	private static Char2ObjectMap<BinaryDecisionDiagram> characterPresence(Collection<CharacterTransition> characterTransitions, Collection<CharacterClassTransition> characterClassTransitions, AllVariables allVariables) {
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			characterPresences.put(characterTransition.character(), characterPresence(allVariables, characterTransition, characterClassTransitions));
		}
		return characterPresences;
	}

	private static BinaryDecisionDiagram characterPresence(AllVariables allVariables, CharacterTransition characterTransition, Collection<CharacterClassTransition> characterClassTransitions) {
		BinaryDecisionDiagram presence = allVariables.specifyCharacterVariables(characterTransition);
		return appendCharacterClasses(allVariables, characterClassTransitions, presence, characterTransition.character());
	}

	private static BinaryDecisionDiagram appendCharacterClasses(AllVariables allVariables, Collection<CharacterClassTransition> characterClassTransitions, BinaryDecisionDiagram presence, char character) {
		for (CharacterClassTransition transition : characterClassTransitions) {
			if (transition.characterClass().contains(character)) {
				presence = presence.orTo(allVariables.specifyCharacterVariables(transition));
			}
		}
		return presence;
	}

	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		frontier = frontier.andTo(characterPresence(character));
		frontier = frontier.relativeProductTo(transitions, existsFromStateAndCharacter);
		return frontier.replaceTo(relabelToStateToFromState);
	}

	private BinaryDecisionDiagram characterPresence(char character) {
		BinaryDecisionDiagram characterPresence = characterPresences.get(character);
		if (characterPresence == null) {
			characterPresence = appendCharacterClasses(allVariables, characterClassTransitions, allVariables.nothing(), character);
			characterPresences.put(character, characterPresence);
		}
		return characterPresence;
	}
}

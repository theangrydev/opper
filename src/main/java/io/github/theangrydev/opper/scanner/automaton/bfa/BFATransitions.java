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

    /**
     * This map gives the BDD for each character that represents all the possible ways that this character can
     * contribute to a transition, in terms of the direct character transition and all the indirrect character classes,
     * if any exist for this character.
     */
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
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = knownCharacterPresences(nfa.characterTransitions(), characterClassTransitions, allVariables);
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

    /**
     * Start off the {@link #characterPresences} with all the characters we already know about that were mentioned in the grammar.
     */
	private static Char2ObjectMap<BinaryDecisionDiagram> knownCharacterPresences(Collection<CharacterTransition> characterTransitions, Collection<CharacterClassTransition> characterClassTransitions, AllVariables allVariables) {
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			characterPresences.put(characterTransition.character(), knownCharacterPresence(allVariables, characterTransition, characterClassTransitions));
		}
		return characterPresences;
	}

	private static BinaryDecisionDiagram knownCharacterPresence(AllVariables allVariables, CharacterTransition characterTransition, Collection<CharacterClassTransition> characterClassTransitions) {
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

    /**
     * Transition from the current frontier of possible states using the transitions that are possible using the given
     * character to a new set of possible states.
     *
     * Let F = f1 or f2 or ... be the set of from states.
     * Let C = c1 or c2 or ... be the set of possible transitions using the given character.
     * Let A = (f1 and c1 and t1) or (f1 and c2 and t2) or ... be the transition table of all possible transitions.
     * Let T be the set of possible to states given F, C and A.
     * Let F' be the possible to states relabled as from states.
     *
     * Then T = exists T. (F and C and A) and F' = relabel T
     */
	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		frontier = frontier.andTo(unseenCharacterPresence(character));
		frontier = frontier.relativeProductTo(transitions, existsFromStateAndCharacter);
		return frontier.replaceTo(relabelToStateToFromState);
	}

    /**
     * Any character that is not already in {@link #characterPresences} must only be able to contribute to a transition
     * through the character classes that it may belong to. This is because if it were part of a direct transition it
     * would already have been precomputed in {@link #knownCharacterPresences(Collection, Collection, AllVariables)}.
     */
	private BinaryDecisionDiagram unseenCharacterPresence(char character) {
		BinaryDecisionDiagram characterPresence = characterPresences.get(character);
		if (characterPresence == null) {
			characterPresence = appendCharacterClasses(allVariables, characterClassTransitions, allVariables.nothing(), character);
			characterPresences.put(character, characterPresence);
		}
		return characterPresence;
	}
}

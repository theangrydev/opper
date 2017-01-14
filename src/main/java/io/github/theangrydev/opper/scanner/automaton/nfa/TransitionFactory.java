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
package io.github.theangrydev.opper.scanner.automaton.nfa;

import io.github.theangrydev.opper.scanner.definition.CharacterClass;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TransitionFactory {

    private Char2ObjectMap<CharacterTransition> characterTransitionsByCharacter;
    private Map<CharacterClass, CharacterClassTransition> characterClassTransitionByCharacterClass;
    private int idSequence;

    public TransitionFactory() {
        characterTransitionsByCharacter = new Char2ObjectArrayMap<>();
        characterClassTransitionByCharacterClass = new HashMap<>();
    }

    public Transition characterTransition(char character) {
        CharacterTransition transition = characterTransitionsByCharacter.get(character);
        if (transition == null) {
            transition = new CharacterTransition(idSequence++, character);
            characterTransitionsByCharacter.put(character, transition);
        }
        return transition;
    }

    public Transition characterClassTransition(CharacterClass characterClass) {
        CharacterClassTransition transition = characterClassTransitionByCharacterClass.get(characterClass);
        if (transition == null) {
            transition = new CharacterClassTransition(idSequence++, characterClass);
            characterClassTransitionByCharacterClass.put(characterClass, transition);
        }
        return transition;
    }

    public Collection<CharacterClassTransition> characterClassTransitions() {
        return characterClassTransitionByCharacterClass.values();
    }

    public Collection<CharacterTransition> characterTransitions() {
        return characterTransitionsByCharacter.values();
    }
}

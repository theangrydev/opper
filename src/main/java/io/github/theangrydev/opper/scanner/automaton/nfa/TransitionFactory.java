/*
 * Copyright 2015-2020 Liam Williams <liam.williams@zoho.com>.
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

    private final Char2ObjectMap<CharacterTransition> characterTransitionsByCharacter;
    private final Map<CharacterClass, CharacterClassTransition> characterClassTransitionByCharacterClass;

    private int idSequence;

    public TransitionFactory() {
        characterTransitionsByCharacter = new Char2ObjectArrayMap<>();
        characterClassTransitionByCharacterClass = new HashMap<>();
    }

    public Transition characterTransition(char character) {
        return characterTransitionsByCharacter.computeIfAbsentPartial(character, this::newCharacterTransition);
    }

    public Transition characterClassTransition(CharacterClass characterClass) {
        return characterClassTransitionByCharacterClass.computeIfAbsent(characterClass, this::newCharacterClassTransition);
    }

    public Collection<CharacterClassTransition> characterClassTransitions() {
        return characterClassTransitionByCharacterClass.values();
    }

    public Collection<CharacterTransition> characterTransitions() {
        return characterTransitionsByCharacter.values();
    }

    private CharacterClassTransition newCharacterClassTransition(CharacterClass characterClass) {
        return new CharacterClassTransition(idSequence++, characterClass);
    }

    private CharacterTransition newCharacterTransition(char character) {
        return new CharacterTransition(idSequence++, character);
    }
}

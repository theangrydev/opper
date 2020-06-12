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

public class CharacterClassTransition implements Transition {
    private int id;
    private final CharacterClass characterClass;

    public CharacterClassTransition(int id, CharacterClass characterClass) {
        this.id = id;
        this.characterClass = characterClass;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void label(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + id + "]" + characterClass;
    }

    public CharacterClass characterClass() {
        return characterClass;
    }
}

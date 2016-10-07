/*
 * Copyright 2015 Liam Williams <liam.williams@zoho.com>.
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

public class CharacterTransition implements Transition {

	private final char character;
	private int id;

	public CharacterTransition(int id, char character) {
		this.id = id;
		this.character = character;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public void label(int id) {
		this.id = id;
	}

	public char character() {
		return character;
	}

	@Override
	public String toString() {
		return "[" + id + "]" + character;
	}
}

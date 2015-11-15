/**
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

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.definition.CharacterClass;

public class SymbolOwnedStateGenerator {

	private final Symbol createdBy;
	private final StateFactory stateFactory;
	private final TransitionFactory transitionFactory;

	public SymbolOwnedStateGenerator(Symbol createdBy, StateFactory stateFactory, TransitionFactory transitionFactory) {
		this.createdBy = createdBy;
		this.stateFactory = stateFactory;
		this.transitionFactory = transitionFactory;
	}

	public State newState() {
		return stateFactory.stateCreatedBy(createdBy);
	}

	public Transition characterTransition(char character) {
		return transitionFactory.characterTransition(character);
	}

	public Transition characterClassTransition(CharacterClass characterClass) {
		return transitionFactory.characterClassTransition(characterClass);
	}
}

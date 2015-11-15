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

import java.util.ArrayList;
import java.util.List;

public class StateFactory {

	private List<State> states;
	private int idSequence;

	public StateFactory() {
		states = new ArrayList<>();
	}

	public State anonymousState() {
		return stateCreatedBy(null);
	}

	public State acceptingState() {
		return stateCreatedBy(null, true);
	}

	public State stateCreatedBy(Symbol createdBy) {
		return stateCreatedBy(createdBy, false);
	}

	private State stateCreatedBy(Symbol createdBy, boolean isAccepting) {
		State state = new State(createdBy, idSequence++, isAccepting);
		states.add(state);
		return state;
	}

	public List<State> states() {
		return states;
	}
}

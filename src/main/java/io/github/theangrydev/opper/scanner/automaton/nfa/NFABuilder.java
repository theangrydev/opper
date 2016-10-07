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

import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.util.List;

public class NFABuilder {

	public static NFA convertToNFA(List<SymbolDefinition> symbolDefinitions) {
		StateFactory stateFactory = new StateFactory();
		TransitionFactory transitionFactory = new TransitionFactory();
		State initial = stateFactory.anonymousState();
		State accepting = stateFactory.acceptingState();
		for (SymbolDefinition symbolDefinition : symbolDefinitions) {
			SymbolOwnedStateGenerator generator = symbolDefinition.stateGenerator(stateFactory, transitionFactory);
			State from = generator.newState();
			State to = generator.newState();
			initial.addNullTransition(from);
			symbolDefinition.populate(generator, from, to);
			to.addNullTransition(accepting);
		}
		return new NFA(initial, stateFactory.states(), transitionFactory.characterClassTransitions(), transitionFactory.characterTransitions());
	}
}

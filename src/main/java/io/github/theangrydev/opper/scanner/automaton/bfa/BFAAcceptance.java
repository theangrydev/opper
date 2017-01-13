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

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;

public class BFAAcceptance {

	private final SymbolForAssignment symbolForAssignment;
	private final BinaryDecisionDiagram acceptingStates;
	private final BinaryDecisionDiagram acceptCheckBuffer;
	private final int[] fromStateAssignmentBuffer;

	private BFAAcceptance(BinaryDecisionDiagram acceptCheckBuffer, BinaryDecisionDiagram acceptingStates, SymbolForAssignment symbolForAssignment, int[] fromStateAssignmentBuffer) {
		this.acceptCheckBuffer = acceptCheckBuffer;
		this.acceptingStates = acceptingStates;
		this.symbolForAssignment = symbolForAssignment;
		this.fromStateAssignmentBuffer = fromStateAssignmentBuffer;
	}

	public static BFAAcceptance bfaAcceptance(NFA nfa, AllVariables allVariables) {
		BinaryDecisionDiagram acceptingStates = acceptingStates(nfa, allVariables);
		SymbolForAssignment symbolForAssignment = SymbolForAssignment.make(nfa, allVariables);
		return new BFAAcceptance(allVariables.anything(), acceptingStates, symbolForAssignment, allVariables.assignmentBuffer());
	}

	private static BinaryDecisionDiagram acceptingStates(NFA nfa, AllVariables allVariables) {
		BinaryDecisionDiagram acceptingStates = allVariables.nothing();
		for (State specifiedAcceptingState : nfa.acceptanceStates()) {
			BinaryDecisionDiagram acceptingState = allVariables.specifyFromVariables(specifiedAcceptingState);
			acceptingStates = acceptingStates.orTo(acceptingState);
		}
		return acceptingStates;
	}

	public Symbol acceptingSymbol(BinaryDecisionDiagram fromFrontier) {
		BinaryDecisionDiagram acceptCheck = acceptingStates.and(fromFrontier, acceptCheckBuffer);
		if (acceptCheck.isZero()) {
			throw new IllegalStateException("The given frontier does not correspond to an accepting state!");
		}
		int[] fromStateAssignment = acceptCheck.oneSatisfyingAssignment(fromStateAssignmentBuffer);
		return symbolForAssignment.symbolForFromState(fromStateAssignment);
	}
}

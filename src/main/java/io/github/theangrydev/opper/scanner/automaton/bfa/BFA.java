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
package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;

public class BFA {

    private final BFATransitions bfaTransitions;
    private final BFAAcceptance bfaAcceptance;
    private final BinaryDecisionDiagram initialState;

    public BFA(BFATransitions bfaTransitions, BFAAcceptance bfaAcceptance, BinaryDecisionDiagram initialState) {
        this.bfaTransitions = bfaTransitions;
        this.bfaAcceptance = bfaAcceptance;
        this.initialState = initialState;
    }

    public BinaryDecisionDiagram initialState() {
        return initialState.copy();
    }

    public Symbol acceptingSymbol(BinaryDecisionDiagram frontier) {
        return bfaAcceptance.acceptingSymbol(frontier);
    }

    public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
        return bfaTransitions.transition(frontier, character);
    }
}

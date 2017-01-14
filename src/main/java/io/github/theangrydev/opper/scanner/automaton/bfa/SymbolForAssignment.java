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

import java.util.List;

public class SymbolForAssignment {

    private final List<Symbol> symbolsByStateId;
    private final AllVariables allVariables;

    private SymbolForAssignment(AllVariables allVariables, List<Symbol> symbolsByStateId) {
        this.allVariables = allVariables;
        this.symbolsByStateId = symbolsByStateId;
    }

    public static SymbolForAssignment make(NFA nfa, AllVariables allVariables) {
        return new SymbolForAssignment(allVariables, nfa.symbolsByStateId());
    }

    public Symbol symbolForFromState(int[] fromStateAssignment) {
        return symbolsByStateId.get(allVariables.fromStateId(fromStateAssignment));
    }

}

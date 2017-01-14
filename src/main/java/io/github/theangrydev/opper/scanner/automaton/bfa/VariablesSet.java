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

import java.util.BitSet;

public class VariablesSet {

    private final BitSet setVariables;

    public VariablesSet(BitSet setVariables) {
        this.setVariables = setVariables;
    }

    public boolean contains(Variable variable) {
        return contains(variable.id());
    }

    public boolean contains(int variableId) {
        return setVariables.get(variableId - 1);
    }
}

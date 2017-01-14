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
package io.github.theangrydev.opper.grammar;

import java.util.HashSet;
import java.util.Set;

public class SymbolFactory {

    private final Set<String> usedNames;
    private int idSequence;

    public SymbolFactory() {
        this.usedNames = new HashSet<>();
    }

    public Symbol createSymbol(String name) {
        if (!usedNames.add(name)) {
            throw new IllegalArgumentException("Symbol name '" + name + "' is already used");
        }
        return new Symbol(idSequence++, name);
    }
}

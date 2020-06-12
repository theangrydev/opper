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
package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.SymbolOwnedStateGenerator;

public class RepeatExpression implements Expression {

    private final Expression expressionToRepeat;

    private RepeatExpression(Expression expressionToRepeat) {
        this.expressionToRepeat = expressionToRepeat;
    }

    public static RepeatExpression repeat(Expression expressionToRepeat) {
        return new RepeatExpression(expressionToRepeat);
    }

    @Override
    public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
        from.addNullTransition(to);

        State repeatStart = generator.newState();
        from.addNullTransition(repeatStart);

        State repeatEnd = generator.newState();
        repeatEnd.addNullTransition(to);
        repeatEnd.addNullTransition(repeatStart);

        expressionToRepeat.populate(generator, repeatStart, repeatEnd);
    }

    @Override
    public String toString() {
        return expressionToRepeat + "*";
    }
}

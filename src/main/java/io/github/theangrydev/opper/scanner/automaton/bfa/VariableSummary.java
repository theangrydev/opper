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

import com.google.common.math.IntMath;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.Transition;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.math.RoundingMode;
import java.util.BitSet;

public class VariableSummary {

    private static final int MIN_FROM_STATE_VARIABLE = 1;

    private final int bitsForStates;
    private final int bitsForCharacters;
    private final int bitsPerRow;

    private final int maxFromStateVariable;
    private final int minCharacterVariable;
    private final int maxCharacterVariable;
    private final int minToStateVariable;
    private final int maxToStateVariable;

    public VariableSummary(int numberOfStates, int numberOfCharacters) {
        this.bitsForStates = IntMath.log2(numberOfStates, RoundingMode.FLOOR) + 1;
        this.bitsForCharacters = IntMath.log2(numberOfCharacters, RoundingMode.FLOOR) + 1;
        this.bitsPerRow = bitsForStates * 2 + bitsForCharacters;
        this.maxFromStateVariable = MIN_FROM_STATE_VARIABLE + bitsForStates - 1;
        this.minCharacterVariable = maxFromStateVariable + 1;
        this.maxCharacterVariable = minCharacterVariable + bitsForCharacters - 1;
        this.minToStateVariable = maxCharacterVariable + 1;
        this.maxToStateVariable = minToStateVariable + bitsForStates - 1;
    }

    public VariablesSet variablesSetInTransition(State from, Transition via, State to) {
        BitSet setVariables = new BitSet(bitsPerRow());
        blastBits(projectFromId(from), setVariables);
        blastBits(projectCharacterId(via), setVariables);
        blastBits(projectToId(to), setVariables);
        return new VariablesSet(setVariables);
    }

    public VariablesSet variablesSetForFromState(State state) {
        return new VariablesSet(BitSet.valueOf(new long[]{projectFromId(state)}));
    }

    public VariablesSet variablesSetForTransition(Transition characterTransition) {
        return new VariablesSet(BitSet.valueOf(new long[]{projectCharacterId(characterTransition)}));
    }

    /**
     * This is approximately 2 * log2(S) + log2(T) where S is the number of states and T is the number of transitions.
     */
    public int bitsPerRow() {
        return bitsPerRow;
    }

    public int fromStateBitPositionForVariableId(int variableId) {
        return variableId - MIN_FROM_STATE_VARIABLE;
    }

    public boolean isCharacter(Variable variable) {
        return variable.id() >= minCharacterVariable && variable.id() <= maxCharacterVariable;
    }

    public boolean isToState(Variable variable) {
        return variable.id() >= minToStateVariable && variable.id() <= maxToStateVariable;
    }

    public boolean isFromState(Variable variable) {
        return variable.id() >= MIN_FROM_STATE_VARIABLE && variable.id() <= maxFromStateVariable;
    }

    public boolean isFromStateOrCharacter(Variable variable) {
        return isFromState(variable) || isCharacter(variable);
    }

    public IntSet allVariableIds() {
        IntSet allVariables = new IntOpenHashSet(bitsPerRow);
        for (int i = 1; i <= bitsPerRow; i++) {
            allVariables.add(i);
        }
        return allVariables;
    }

    private int projectFromId(State from) {
        return from.id();
    }

    private int projectCharacterId(Transition transition) {
        return transition.id() << bitsForStates;
    }

    private int projectToId(State to) {
        return to.id() << (bitsForStates + bitsForCharacters);
    }

    private static void blastBits(long number, BitSet row) {
        row.or(BitSet.valueOf(new long[]{number}));
    }

    @Override
    public String toString() {
        return "BitSummary{" +
                "bitsForStates=" + bitsForStates +
                ", bitsForCharacters=" + bitsForCharacters +
                ", bitsPerRow=" + bitsPerRow +
                '}';
    }
}

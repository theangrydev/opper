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

import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TransitionTable {
    private final List<VariablesSet> transitions;

    private TransitionTable(List<VariablesSet> transitions) {
        this.transitions = transitions;
    }

    public static TransitionTable fromNFA(NFA nfa) {
        List<VariablesSet> transitions = new ArrayList<>();
        VariableSummary variableSummary = nfa.variableSummary();
        nfa.visitTransitions((from, via, to) -> transitions.add(variableSummary.variablesSetInTransition(from, via, to)));
        return new TransitionTable(transitions);
    }

    public List<VariablesSet> transitions() {
        return transitions;
    }

    public boolean isEmpty() {
        return transitions.isEmpty();
    }

    public int size() {
        return transitions.size();
    }

    public TransitionTable rowsWithVariable(int variable) {
        return new TransitionTable(transitions.stream().filter(row -> row.contains(variable)).collect(toList()));
    }

    public TransitionTable rowsWithoutVariable(int variable) {
        return new TransitionTable(transitions.stream().filter(row -> !row.contains(variable)).collect(toList()));
    }

    public int numberOfRowsWithVariable(int variable) {
        return (int) transitions.stream().filter(row -> row.contains(variable)).count();
    }
}

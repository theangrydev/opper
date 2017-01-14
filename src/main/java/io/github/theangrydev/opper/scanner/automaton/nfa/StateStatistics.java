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
package io.github.theangrydev.opper.scanner.automaton.nfa;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.util.stream.Collectors;

public class StateStatistics {

    private Multiset<Transition> transitionFrequencies;
    private Multiset<State> stateFrequencies;

    public StateStatistics() {
        this.transitionFrequencies = HashMultiset.create();
        this.stateFrequencies = HashMultiset.create();
    }

    public void record(State state) {
        state.recordStatistics(this);
    }

    public ImmutableMultiset<Transition> transitionFrequencies() {
        return Multisets.copyHighestCountFirst(transitionFrequencies);
    }

    public ImmutableMultiset<State> stateFrequencies() {
        return Multisets.copyHighestCountFirst(stateFrequencies);
    }

    public void recordCharacter(Transition transition, int times) {
        transitionFrequencies.add(transition, times);
    }

    public void recordState(State state) {
        recordState(state, 1);
    }

    public void recordState(State state, int times) {
        stateFrequencies.add(state, times);
    }

    @Override
    public String toString() {
        return "StateStatistics{" +
                "transitionFrequencies=" + print(transitionFrequencies) +
                ", stateFrequencies=" + print(stateFrequencies) +
                '}';
    }

    private String print(Multiset<?> frequencies) {
        ImmutableMultiset<?> highestFirst = Multisets.copyHighestCountFirst(frequencies);
        return highestFirst.elementSet().stream().map(element -> element + ":" + highestFirst.count(element)).collect(Collectors.joining("\n", "\n", "\n"));
    }
}

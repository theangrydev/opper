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

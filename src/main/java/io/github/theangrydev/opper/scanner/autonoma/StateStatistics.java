package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.util.stream.Collectors;

public class StateStatistics {

	private Multiset<Character> characterFrequencies;
	private Multiset<State> stateFrequencies;

	public StateStatistics() {
		this.characterFrequencies = HashMultiset.create();
		this.stateFrequencies = HashMultiset.create();
	}

	public void recordCharacter(Character character, int times) {
		characterFrequencies.add(character, times);
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
			"characterFrequencies=" + print(characterFrequencies) +
			", stateFrequencies=" + print(stateFrequencies) +
			'}';
	}

	private String print(Multiset<?> frequencies) {
		ImmutableMultiset<?> highestFirst = Multisets.copyHighestCountFirst(frequencies);
		return highestFirst.elementSet().stream().map(element -> element + ":" + highestFirst.count(element)).collect(Collectors.joining("\n", "\n", "\n"));
	}
}

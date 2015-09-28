package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.Multiset;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class NFA {
	private final State initialState;
	private final List<CharacterTransition> characterTransitions;
	private List<State> states;

	public NFA(State initialState, List<State> states, List<CharacterTransition> characterTransitions) {
		this.initialState = initialState;
		this.states = states;
		this.characterTransitions = characterTransitions;
	}

	public void removeEpsilionTransitions() {
		states.forEach(State::eliminateEpsilonTransitions);
	}

	public void removeUnreachableStates() {
		initialState.markReachableStates();
		states = states.stream().filter(State::wasReached).collect(toList());
	}

	public List<State> states() {
		return states;
	}

	public State initialState() {
		return initialState;
	}

	public int numberOfStates() {
		return states.size();
	}

	public int numberOfTransitions() {
		return characterTransitions.size();
	}

	public List<CharacterTransition> characterTransitions() {
		return characterTransitions;
	}

	public void relabelAccordingToFrequencies() {
		StateStatistics stateStatistics = computeStateStatistics();
		relabel(stateStatistics.stateFrequencies());
		relabel(stateStatistics.transitionFrequencies());
	}

	private void relabel(Multiset<? extends Identifiable> frequencies) {
		int idSequence = 1;
		for (Multiset.Entry<? extends Identifiable> entry : frequencies.entrySet()) {
			entry.getElement().label(idSequence++);
		}
	}

	private StateStatistics computeStateStatistics() {
		StateStatistics stateStatistics = new StateStatistics();
		states.forEach(stateStatistics::record);
		return stateStatistics;
	}
}

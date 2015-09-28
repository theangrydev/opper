package io.github.theangrydev.opper.scanner.autonoma;

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
		initialState.markReachableStates();
		states = states.stream().filter(State::wasReached).collect(toList());
	}

	public List<State> states() {
		return states;
	}

	public State initialState() {
		return initialState;
	}

	public List<CharacterTransition> characterTransitions() {
		return characterTransitions;
	}

	public StateStatistics computeStateStatistics() {
		StateStatistics stateStatistics = new StateStatistics();
		states.forEach(stateStatistics::record);
		return stateStatistics;
	}
}

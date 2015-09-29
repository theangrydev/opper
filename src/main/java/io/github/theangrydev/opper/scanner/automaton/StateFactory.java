package io.github.theangrydev.opper.scanner.automaton;

import io.github.theangrydev.opper.grammar.Symbol;

import java.util.ArrayList;
import java.util.List;

public class StateFactory {

	private List<State> states;
	private int idSequence;

	public StateFactory() {
		states = new ArrayList<>();
	}

	public State anonymousState() {
		return stateCreatedBy(null);
	}

	public State acceptingState() {
		return stateCreatedBy(null, true);
	}

	public State stateCreatedBy(Symbol createdBy) {
		return stateCreatedBy(createdBy, false);
	}

	private State stateCreatedBy(Symbol createdBy, boolean isAccepting) {
		State state = new State(createdBy, idSequence++, isAccepting);
		states.add(state);
		return state;
	}

	public List<State> states() {
		return states;
	}
}

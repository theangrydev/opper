package io.github.theangrydev.opper.scanner.autonoma;

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

	public State stateCreatedBy(Symbol createdBy) {
		State state = new State(createdBy, idSequence++);
		states.add(state);
		return state;
	}

	public List<State> states() {
		return states;
	}
}

package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class State {
	private final Symbol createdBy;
	private final int id;
	private Char2ObjectMap<List<State>> characterTransitions;
	private List<State> nullTransitions;

	public State(Symbol createdBy, int id) {
		this.createdBy = createdBy;
		this.id = id;
		characterTransitions = new Char2ObjectArrayMap<>();
		nullTransitions = new ArrayList<>();
	}

	public void addNullTransition(State state) {
		nullTransitions.add(state);
	}

	public void addTransition(char state, State to) {
		List<State> transitions = characterTransitions.get(state);
		if (transitions == null) {
			transitions = new ArrayList<>();
			characterTransitions.put(state, transitions);
		}
		transitions.add(to);
	}

	@Override
	public String toString() {
		return "S[" + id + "], createdBy=" + createdBy + ", characterTransitions=" + printCharacterTransitions() + ", nullTransitions=" + print(nullTransitions);
	}

	private String print(List<State> states) {
		return states.stream().map(state -> state.id).map(String::valueOf).collect(Collectors.joining(","));
	}

	private String printCharacterTransitions() {
		return characterTransitions.entrySet().stream().map(entry -> entry.getKey() + "->" + print(entry.getValue())).collect(Collectors.joining(","));
	}
}

package io.github.theangrydev.opper.scanner.automaton.nfa;

import io.github.theangrydev.opper.grammar.Symbol;

import java.util.*;
import java.util.stream.Collectors;

public class State implements Identifiable {
	private final Set<Symbol> reachedBySymbols;
	private int id;
	private Map<Transition, List<State>> transitions;
	private List<State> epsilonTransitions;
	private boolean isAccepting;
	private Set<State> reachableStates;
	private boolean reached;

	public State(Symbol reachedBySymbols, int id, boolean isAccepting) {
		this.reachedBySymbols = new HashSet<>();
		if (reachedBySymbols != null) {
			this.reachedBySymbols.add(reachedBySymbols);
		}
		this.id = id;
		this.isAccepting = isAccepting;
		transitions = new HashMap<>();
		epsilonTransitions = new ArrayList<>();
	}

	public interface TransitionVisitor {
		void visit(State from, Transition via, State to);
	}

	@Override
	public int id() {
		return id;
	}

	public Symbol reachableBy() {
		return reachedBySymbols.iterator().next();
	}

	public void visitTransitions(TransitionVisitor transitionVisitor) {
		transitions.entrySet().forEach(entry -> {
			Transition transition = entry.getKey();
			List<State> states = entry.getValue();
			states.forEach(state -> transitionVisitor.visit(this, transition, state));
		});
	}

	public void addNullTransition(State state) {
		epsilonTransitions.add(state);
	}

	public void addTransition(Transition via, State to) {
		List<State> transitions = this.transitions.get(via);
		if (transitions == null) {
			transitions = new ArrayList<>();
			this.transitions.put(via, transitions);
		}
		transitions.add(to);
	}

	public boolean wasReached() {
		return reached;
	}

	public void recordStatistics(StateStatistics stateStatistics) {
		transitions.entrySet().forEach(entry -> {
			Transition transition = entry.getKey();
			List<State> states = entry.getValue();
			int times = states.size();
			stateStatistics.recordCharacter(transition, times);
			stateStatistics.recordState(this, times);
			states.forEach(stateStatistics::recordState);
		});
	}

	public void markReachableStates() {
		if (!reached) {
			reached = true;
			transitions.values().forEach(states -> states.forEach(State::markReachableStates));
		}
	}

	public void eliminateEpsilonTransitions() {
		Set<State> reachableStates = reachableByEpsilonTransitions();
		addReachableTransitions(reachableStates);
		determineIfThisStateIsAccepting(reachableStates);
		removeEpsilonTransitions();
	}

	@Override
	public void label(int id) {
		this.id = id;
	}

	private void removeEpsilonTransitions() {
		epsilonTransitions = Collections.emptyList();
	}

	private void determineIfThisStateIsAccepting(Set<State> reachableStates) {
		isAccepting = isAccepting || reachableStates.stream().anyMatch(State::isAccepting);
	}

	public boolean isAccepting() {
		return isAccepting;
	}

	private void addReachableTransitions(Set<State> reachableStates) {
		for (State reachableState : reachableStates) {
			reachedBySymbols.addAll(reachableState.reachedBySymbols);
			transitions.putAll(reachableState.transitions);
		}
	}

	public Set<State> reachableByEpsilonTransitions() {
		if (reachableStates != null) {
			return reachableStates;
		}
		reachableStates = new HashSet<>();
		reachableStates.addAll(epsilonTransitions);
		epsilonTransitions.stream().map(State::reachableByEpsilonTransitions).forEach(reachableStates::addAll);
		return reachableStates;
	}

	@Override
	public String toString() {
		return "S[" + id + "], createdBy=" + reachedBySymbols + ", characterTransitions=" + printCharacterTransitions() + ", nullTransitions=" + print(epsilonTransitions) + ", isAccepting=" + isAccepting;
	}

	private String print(List<State> states) {
		return states.stream().map(state -> state.id).map(String::valueOf).collect(Collectors.joining(","));
	}

	private String printCharacterTransitions() {
		return transitions.entrySet().stream().map(entry -> entry.getKey() + "->" + print(entry.getValue())).collect(Collectors.joining(","));
	}
}

package io.github.theangrydev.opper.scanner.automaton.nfa;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.bfa.VariableSummary;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class NFA {
	private final Collection<CharacterClassTransition> characterClassTransitions;
	private final Collection<CharacterTransition> characterTransitions;
	private List<State> initialStates;
	private List<State> states;

	public NFA(State initialState, List<State> states, Collection<CharacterClassTransition> characterClassTransitions, Collection<CharacterTransition> characterTransitions) {
		this.characterClassTransitions = characterClassTransitions;
		this.initialStates = Lists.newArrayList(initialState);
		this.states = states;
		this.characterTransitions = characterTransitions;
	}

	public void removeEpsilionTransitions() {
		initialStates = Lists.newArrayList(initialStates.get(0).reachableByEpsilonTransitions());
		states.forEach(State::eliminateEpsilonTransitions);
	}

	public void removeUnreachableStates() {
		initialStates.forEach(State::markReachableStates);
		states = states.stream().filter(State::wasReached).collect(toList());
	}

	public List<State> initialStates() {
		return initialStates;
	}

	public VariableSummary variableSummary() {
		return new VariableSummary(states.size(), characterTransitions.size() + characterClassTransitions.size());
	}

	public Collection<CharacterTransition> characterTransitions() {
		return characterTransitions;
	}

	public Collection<CharacterClassTransition> characterClassTransitions() {
		return characterClassTransitions;
	}

	public void visitTransitions(State.TransitionVisitor transitionVisitor) {
		states.forEach(state -> state.visitTransitions(transitionVisitor));
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

	public List<State> acceptanceStates() {
		return states.stream().filter(State::isAccepting).collect(toList());
	}

	public List<Symbol> symbolsByStateId() {
		return concat(Stream.of((Symbol) null), states.stream().sorted(comparing(State::id)).map(State::reachableBy)).collect(toList());
	}
}

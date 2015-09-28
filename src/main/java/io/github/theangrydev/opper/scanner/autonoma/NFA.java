package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.Multiset;
import io.github.theangrydev.opper.scanner.autonoma.State.TransitionVisitor;
import io.github.theangrydev.opper.scanner.bdd.VariableSummary;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class NFA {
	private final State initialState;
	private final List<CharacterTransition> characterTransitions;
	private List<State> states;

	private NFA(State initialState, List<State> states, List<CharacterTransition> characterTransitions) {
		this.initialState = initialState;
		this.states = states;
		this.characterTransitions = characterTransitions;
	}

	public static NFA convertToNFA(List<SymbolDefinition> symbolDefinitions) {
		StateFactory stateFactory = new StateFactory();
		TransitionFactory transitionFactory = new TransitionFactory();
		State initial = stateFactory.anonymousState();
		State accepting = stateFactory.acceptingState();
		for (SymbolDefinition symbolDefinition : symbolDefinitions) {
			SymbolOwnedStateGenerator generator = symbolDefinition.stateGenerator(stateFactory, transitionFactory);
			State from = generator.newState();
			initial.addNullTransition(from);
			symbolDefinition.populate(generator, from, accepting);
		}
		return new NFA(initial, stateFactory.states(), transitionFactory.characterTransitions());
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

	public VariableSummary variableSummary() {
		return new VariableSummary(states.size(), characterTransitions.size());
	}

	public List<CharacterTransition> characterTransitions() {
		return characterTransitions;
	}

	public void visitTransitions(TransitionVisitor transitionVisitor) {
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
}

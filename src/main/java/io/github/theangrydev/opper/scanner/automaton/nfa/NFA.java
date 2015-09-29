package io.github.theangrydev.opper.scanner.automaton.nfa;

import com.google.common.collect.Multiset;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.bfa.VariableSummary;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

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
		return concat(Stream.of((Symbol) null), states().stream().sorted(comparing(State::id)).map(State::symbol)).collect(toList());
	}
}

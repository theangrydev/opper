package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.scanner.bdd.BitSummary;

import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.scanner.autonoma.SetVariables.transition;
import static java.util.stream.Collectors.toList;

public class TransitionTable {
	private final List<SetVariables> transitions;

	private TransitionTable(List<SetVariables> transitions) {
		this.transitions = transitions;
	}

	public static TransitionTable fromNFA(NFA nfa) {
		List<SetVariables> transitions = new ArrayList<>();
		BitSummary bitSummary = nfa.bitSummary();
		nfa.visitTransitions((from, via, to) -> transitions.add(transition(bitSummary, from, via, to)));
		return new TransitionTable(transitions);
	}

	public List<SetVariables> transitions() {
		return transitions;
	}

	public boolean isEmpty() {
		return transitions.isEmpty();
	}

	public int size() {
		return transitions.size();
	}

	public TransitionTable rowsWithVariable(int variable) {
		return new TransitionTable(transitions.stream().filter(row -> row.contains(variable)).collect(toList()));
	}

	public TransitionTable rowsWithoutVariable(int variable) {
		return new TransitionTable(transitions.stream().filter(row -> !row.contains(variable)).collect(toList()));
	}

	public int numberOfRowsWithVariable(int variable) {
		return (int) transitions.stream().filter(row -> row.contains(variable)).count();
	}
}

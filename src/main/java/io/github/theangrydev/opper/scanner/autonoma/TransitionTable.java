package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.scanner.bdd.BitSummary;

import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.scanner.autonoma.SetVariables.transition;

public class TransitionTable {
	private final List<SetVariables> transitions;

	public TransitionTable(NFA nfa) {
		transitions = new ArrayList<>();
		BitSummary bitSummary = nfa.bitSummary();
		nfa.visitTransitions((from, via, to) -> transitions.add(transition(bitSummary, from, via, to)));
	}

	public List<SetVariables> transitions() {
		return transitions;
	}
}

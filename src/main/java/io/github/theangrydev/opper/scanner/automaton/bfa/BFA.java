package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;

import java.util.Optional;

public class BFA {

	private final BFATransitions bfaTransitions;
	private final BFAAcceptance bfaAcceptance;
	private final BinaryDecisionDiagram initialState;

	public BFA(BFATransitions bfaTransitions, BFAAcceptance bfaAcceptance, BinaryDecisionDiagram initialState) {
		this.bfaTransitions = bfaTransitions;
		this.bfaAcceptance = bfaAcceptance;
		this.initialState = initialState;
	}

	public BinaryDecisionDiagram initialState() {
		return initialState.copy();
	}

	public Optional<Symbol> checkAcceptance(BinaryDecisionDiagram frontier) {
		return bfaAcceptance.checkAcceptance(frontier);
	}

	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		return bfaTransitions.transition(frontier, character);
	}
}

package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import jdd.bdd.Permutation;

import java.util.Optional;

public class BFA {

	private final BFATransitions bfaTransitions;
	private final BFAAcceptance bfaAcceptance;
	private final BinaryDecisionDiagram initialState;
	private final Permutation relabelToStateToFromState;

	public BFA(BFATransitions bfaTransitions, BFAAcceptance bfaAcceptance, BinaryDecisionDiagram initialState, Permutation relabelToStateToFromState) {
		this.bfaTransitions = bfaTransitions;
		this.bfaAcceptance = bfaAcceptance;
		this.initialState = initialState;
		this.relabelToStateToFromState = relabelToStateToFromState;
	}

	public BinaryDecisionDiagram relabelToStateToFromState(BinaryDecisionDiagram frontier) {
		return frontier.replaceTo(relabelToStateToFromState);
	}

	public BinaryDecisionDiagram initialState() {
		return initialState;
	}

	public Optional<Symbol> checkAcceptance(BinaryDecisionDiagram frontier) {
		return bfaAcceptance.checkAcceptance(frontier);
	}

	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		return bfaTransitions.transition(frontier, character);
	}
}

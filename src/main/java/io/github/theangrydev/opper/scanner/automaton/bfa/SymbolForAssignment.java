package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;

import java.util.List;

public class SymbolForAssignment {

	private final List<Symbol> symbolsByStateId;
	private final AllVariables allVariables;

	private SymbolForAssignment(AllVariables allVariables, List<Symbol> symbolsByStateId) {
		this.allVariables = allVariables;
		this.symbolsByStateId = symbolsByStateId;
	}

	public static SymbolForAssignment make(NFA nfa, AllVariables allVariables) {
		return new SymbolForAssignment(allVariables, nfa.symbolsByStateId());
	}

	public Symbol symbolForToState(int[] toStateAssignment) {
		return symbolsByStateId.get(allVariables.toStateId(toStateAssignment));
	}

}

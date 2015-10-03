package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.StateFactory;
import io.github.theangrydev.opper.scanner.automaton.nfa.SymbolOwnedStateGenerator;
import io.github.theangrydev.opper.scanner.automaton.nfa.TransitionFactory;

public class SymbolDefinition {
	private final Symbol symbol;
	private final Expression expression;

	private SymbolDefinition(Symbol symbol, Expression expression) {
		this.symbol = symbol;
		this.expression = expression;
	}

	public static SymbolDefinition definition(Symbol symbol, Expression expression) {
		return new SymbolDefinition(symbol, expression);
	}

	public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
		expression.populate(generator, from, to);
	}

	public SymbolOwnedStateGenerator stateGenerator(StateFactory stateFactory, TransitionFactory transitionFactory) {
		return new SymbolOwnedStateGenerator(symbol, stateFactory, transitionFactory);
	}
}

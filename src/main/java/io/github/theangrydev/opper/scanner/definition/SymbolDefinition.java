package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.StateFactory;
import io.github.theangrydev.opper.scanner.autonoma.SymbolOwnedStateGenerator;
import io.github.theangrydev.opper.scanner.autonoma.TransitionFactory;

public class SymbolDefinition {
	private final Symbol symbol;
	private final Expression expression;

	public SymbolDefinition(Symbol symbol, Expression expression) {
		this.symbol = symbol;
		this.expression = expression;
	}

	public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
		expression.populate(generator, from, to);
	}

	public SymbolOwnedStateGenerator stateGenerator(StateFactory stateFactory, TransitionFactory transitionFactory) {
		return new SymbolOwnedStateGenerator(symbol, stateFactory, transitionFactory);
	}
}

package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.grammar.Symbol;

public class SymbolOwnedStateGenerator {

	private final Symbol createdBy;
	private final StateFactory stateFactory;

	public SymbolOwnedStateGenerator(Symbol createdBy, StateFactory stateFactory) {
		this.createdBy = createdBy;
		this.stateFactory = stateFactory;
	}

	public State newState() {
		return stateFactory.stateCreatedBy(createdBy);
	}
}

package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.util.List;

public class SymbolDefinitionToStateConverter {

	private final StateFactory stateFactory;
	private final TransitionFactory transitionFactory;

	public SymbolDefinitionToStateConverter(StateFactory stateFactory, TransitionFactory transitionFactory) {
		this.stateFactory = stateFactory;
		this.transitionFactory = transitionFactory;
	}

	public NFA convertDefinitionsToStates(List<SymbolDefinition> symbolDefinitions) {
		State initial = stateFactory.anonymousState();
		State accepting = stateFactory.acceptingState();
		for (SymbolDefinition symbolDefinition : symbolDefinitions) {
			SymbolOwnedStateGenerator generator = symbolDefinition.stateGenerator(stateFactory, transitionFactory);
			State from = generator.newState();
			initial.addNullTransition(from);
			symbolDefinition.populate(generator, from, accepting);
		}
		return new NFA(initial, stateFactory.states());
	}
}

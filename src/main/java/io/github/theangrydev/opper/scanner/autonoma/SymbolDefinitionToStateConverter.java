package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.util.List;

public class SymbolDefinitionToStateConverter {

	private StateFactory stateFactory;

	public SymbolDefinitionToStateConverter(StateFactory stateFactory) {
		this.stateFactory = stateFactory;
	}

	public State convertDefinitionsToStates(List<SymbolDefinition> symbolDefinitions) {
		State initial = stateFactory.anonymousState();
		State accepting = stateFactory.acceptingState();
		for (SymbolDefinition symbolDefinition : symbolDefinitions) {
			SymbolOwnedStateGenerator generator = symbolDefinition.stateGenerator(stateFactory);
			State from = generator.newState();
			initial.addNullTransition(from);
			symbolDefinition.populate(generator, from, accepting);
		}
		stateFactory.eliminateEpsilonTransitions();
		initial.markReachableStates();
		stateFactory.removeUnreachableStates();
		return initial;
	}
}

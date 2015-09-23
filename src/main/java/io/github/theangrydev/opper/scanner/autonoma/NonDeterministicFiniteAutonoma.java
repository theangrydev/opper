package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.util.List;

public class NonDeterministicFiniteAutonoma {

	public NonDeterministicFiniteAutonoma(List<SymbolDefinition> symbolDefinitions) {
		StateFactory stateFactory = new StateFactory();
		State initial = stateFactory.anonymousState();
		for (SymbolDefinition symbolDefinition : symbolDefinitions) {
			SymbolOwnedStateGenerator generator = symbolDefinition.stateGenerator(stateFactory);
			State from = generator.newState();
			State to = generator.newState();
			initial.addNullTransition(from);
			symbolDefinition.populate(generator, from, to);
		}
	}
}

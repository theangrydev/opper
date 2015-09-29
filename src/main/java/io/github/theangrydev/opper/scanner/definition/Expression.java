package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.automaton.State;
import io.github.theangrydev.opper.scanner.automaton.SymbolOwnedStateGenerator;

public interface Expression {
	void populate(SymbolOwnedStateGenerator generator, State from, State to);
}

package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.SymbolOwnedStateGenerator;

public interface Expression {
	void populate(SymbolOwnedStateGenerator generator, State from, State to);
}

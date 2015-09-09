package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.DottedRule;

public interface EarlyOrLeoItem {
	DottedRule transition(Symbol symbol);
	int origin();
}

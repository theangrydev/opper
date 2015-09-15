package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Symbol;

public interface EarlyOrLeoItem {
	DottedRule transition(Symbol symbol);
	int origin();
}

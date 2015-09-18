package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.grammar.Symbol;

public interface EarlyOrLeoItem {
	DottedRule dottedRule();
	DottedRule transition(Symbol symbol);
	int origin();
}

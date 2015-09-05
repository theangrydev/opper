package io.github.theangrydev.opper;

public interface EarlyOrLeoItem {
	DottedRule transition(Symbol symbol);
	int origin();
}

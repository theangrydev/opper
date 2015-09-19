package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public interface EarlyOrLeoItem {
	DottedRule dottedRule();
	DottedRule transition();
	TransitionsEarlySetsBySymbol transitions();
}

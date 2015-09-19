package io.github.theangrydev.opper.recogniser.precomputed.prediction;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.item.DottedRule;

import java.util.List;

public interface RulePrediction {
	List<DottedRule> rulesThatCanBeTriggeredBy(Symbol startSymbol);
	DottedRule initial();
}

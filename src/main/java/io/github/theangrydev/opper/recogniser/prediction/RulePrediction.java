package io.github.theangrydev.opper.recogniser.prediction;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;

import java.util.List;

public interface RulePrediction {
	List<Rule> rulesThatCanBeReachedFrom(Symbol startSymbol);
}

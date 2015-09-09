package io.github.theangrydev.opper.prediction;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;

import java.util.List;

public interface RulePrediction {
	List<Rule> predict(Symbol symbol);
}

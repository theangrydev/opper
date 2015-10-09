package io.github.theangrydev.opper.parser.precomputed.prediction;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.item.DottedRule;

import java.util.List;

public interface RulePrediction {
	List<DottedRule> rulesThatCanBeTriggeredBy(Symbol startSymbol);
	DottedRule initial();
}

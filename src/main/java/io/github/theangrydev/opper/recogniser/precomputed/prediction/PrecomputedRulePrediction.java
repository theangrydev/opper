package io.github.theangrydev.opper.recogniser.precomputed.prediction;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.item.DottedRule;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.List;

public class PrecomputedRulePrediction implements RulePrediction {

	private final DottedRule initial;
	private final ObjectList<List<DottedRule>> predictions;

	public PrecomputedRulePrediction(Grammar grammar, ComputedRulePrediction computedRulePrediction) {
		int symbols = grammar.symbols().size();
		this.predictions = new ObjectArrayList<>(symbols);
		predictions.size(symbols);
		for (Symbol symbol : grammar.symbols()) {
			predictions.set(symbol.id(), computedRulePrediction.rulesThatCanBeTriggeredBy(symbol));
		}
		this.initial = computedRulePrediction.initial();
	}

	@Override
	public List<DottedRule> rulesThatCanBeTriggeredBy(Symbol startSymbol) {
		return predictions.get(startSymbol.id());
	}

	@Override
	public DottedRule initial() {
		return initial;
	}
}
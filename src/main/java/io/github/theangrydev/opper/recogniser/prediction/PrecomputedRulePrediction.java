package io.github.theangrydev.opper.recogniser.prediction;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.List;

public class PrecomputedRulePrediction implements RulePrediction {

	private final ObjectList<List<Rule>> predictions;

	public PrecomputedRulePrediction(Grammar grammar, ComputedRulePrediction computedRulePrediction) {
		int symbols = grammar.symbols().size();
		this.predictions = new ObjectArrayList<>(symbols);
		predictions.size(symbols);
		for (Symbol symbol : grammar.symbols()) {
			predictions.set(symbol.index(), computedRulePrediction.rulesThatCanBeReachedFrom(symbol));
		}
	}

	@Override
	public List<Rule> rulesThatCanBeReachedFrom(Symbol startSymbol) {
		return predictions.get(startSymbol.index());
	}
}

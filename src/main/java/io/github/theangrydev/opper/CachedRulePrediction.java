package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.List;

public class CachedRulePrediction implements RulePrediction {

	private final ObjectList<List<Rule>> cachedPredictions;
	private final RulePrediction delegate;

	public CachedRulePrediction(Grammar grammar, RulePrediction delegate) {
		this.delegate = delegate;
		int symbols = grammar.symbols().size();
		this.cachedPredictions = new ObjectArrayList<>(symbols);
		cachedPredictions.size(symbols);
	}

	@Override
	public List<Rule> predict(Symbol symbol) {
		List<Rule> rules = cachedPredictions.get(symbol.index());
		if (rules == null) {
			rules = delegate.predict(symbol);
			cachedPredictions.set(symbol.index(), rules);
		}
		return rules;
	}
}

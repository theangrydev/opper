package io.github.theangrydev.opper.recogniser.precomputed;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.github.theangrydev.opper.grammar.Rule.triggeredBy;

public class DerivationConsequences {

	private final Function<Rule, Symbol> consequence;
	private final Grammar grammar;

	public DerivationConsequences(Grammar grammar, Function<Rule, Symbol> consequence) {
		this.consequence = consequence;
		this.grammar = grammar;
	}

	public Set<Symbol> of(Symbol symbol) {
		List<Symbol> confirmedConsequences = new ObjectArrayList<Symbol>(){{
			add(symbol);
		}};
		Set<Symbol> uniqueConsequences = new ObjectArraySet<Symbol>(){{
			add(symbol);
		}};
		confirmedConsequences.forEach(confirmedConsequence -> rulesTriggeredBy(confirmedConsequence).map(consequence).forEach(consequence -> {
			boolean wasNew = uniqueConsequences.add(consequence);
			if (wasNew) {
				confirmedConsequences.add(consequence);
			}
		}));
		return uniqueConsequences;
	}

	private Stream<Rule> rulesTriggeredBy(Symbol symbol) {
		return grammar.rules().stream().filter(triggeredBy(symbol));
	}
}

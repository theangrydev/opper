package io.github.theangrydev.opper.recogniser.recursion;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ComputedRightRecursion implements RightRecursion {
	private final Grammar grammar;

	public ComputedRightRecursion(Grammar grammar) {
		this.grammar = grammar;
	}

	@Override
	public boolean isRightRecursive(Rule rule) {
		return rule.isRightRecursive() || isIndirectlyRightRecursive(rule);
	}

	private boolean isIndirectlyRightRecursive(Rule rule) {
		return derivationSuffixes(rule.derivationSuffix()).contains(rule.trigger());
	}

	private Set<Symbol> derivationSuffixes(Symbol symbol) {
		List<Symbol> confirmedSuffixes = new ObjectArrayList<Symbol>(){{
			add(symbol);
		}};
		Set<Symbol> uniqueSuffixes = new ObjectArraySet<Symbol>(){{
			add(symbol);
		}};
		confirmedSuffixes.forEach(confirmedSuffix -> rulesTriggeredBy(confirmedSuffix).map(Rule::derivationSuffix).forEach(derivationSuffix -> {
			boolean wasNew = uniqueSuffixes.add(derivationSuffix);
			if (wasNew) {
				confirmedSuffixes.add(derivationSuffix);
			}
		}));
		return uniqueSuffixes;
	}

	private Stream<Rule> rulesTriggeredBy(Symbol symbol) {
		return grammar.rules().stream().filter(triggeredBy(symbol));
	}

	private Predicate<Rule> triggeredBy(Symbol symbol) {
		return rule -> symbol.equals(rule.trigger());
	}
}

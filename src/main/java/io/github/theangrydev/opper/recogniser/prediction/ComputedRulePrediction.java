package io.github.theangrydev.opper.recogniser.prediction;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ComputedRulePrediction implements RulePrediction {

	private final Logger logger;
	private final Grammar grammar;

	public ComputedRulePrediction(Logger logger, Grammar grammar) {
		this.logger = logger;
		this.grammar = grammar;
	}

	@Override
	public List<Rule> rulesThatCanBeTriggeredBy(Symbol symbol) {
		Set<Symbol> derivationPrefixes = derivationPrefixes(symbol);
		logger.log(() -> "Derivation prefixes are: " + derivationPrefixes);
		List<Rule> applicableRules = rulesTriggeredBy(derivationPrefixes);
		logger.log(() -> "Applicable rules are: " + applicableRules);
		return applicableRules;
	}

	private List<Rule> rulesTriggeredBy(Set<Symbol> symbols) {
		return grammar.rules().stream().filter(triggeredByOneOf(symbols)).collect(toList());
	}

	private Predicate<Rule> triggeredByOneOf(Set<Symbol> symbols) {
		return rule -> symbols.contains(rule.trigger());
	}

	private Set<Symbol> derivationPrefixes(Symbol symbol) {
		List<Symbol> confirmedPrefixes = new ObjectArrayList<Symbol>(){{
			add(symbol);
		}};
		Set<Symbol> uniquePrefixes = new ObjectArraySet<Symbol>(){{
			add(symbol);
		}};
		confirmedPrefixes.forEach(confirmedPrefix -> rulesTriggeredBy(confirmedPrefix).map(Rule::derivationPrefix).forEach(derivationPrefix -> {
			boolean wasNew = uniquePrefixes.add(derivationPrefix);
			if (wasNew) {
				confirmedPrefixes.add(derivationPrefix);
			}
		}));
		return uniquePrefixes;
	}

	private Stream<Rule> rulesTriggeredBy(Symbol symbol) {
		return grammar.rules().stream().filter(triggeredBy(symbol));
	}

	private Predicate<Rule> triggeredBy(Symbol symbol) {
		return rule -> symbol.equals(rule.trigger());
	}
}

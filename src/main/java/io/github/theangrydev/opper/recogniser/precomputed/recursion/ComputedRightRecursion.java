package io.github.theangrydev.opper.recogniser.precomputed.recursion;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.recogniser.precomputed.DerivationConsequences;

public class ComputedRightRecursion implements RightRecursion {

	private final DerivationConsequences derivationSuffixes;

	public ComputedRightRecursion(Grammar grammar) {
		this.derivationSuffixes = new DerivationConsequences(grammar, Rule::symbols);
	}

	@Override
	public boolean isRightRecursive(Rule rule) {
		return rule.isRightRecursive() || isIndirectlyRightRecursive(rule);
	}

	private boolean isIndirectlyRightRecursive(Rule rule) {
		return derivationSuffixes.of(rule.symbols()).contains(rule.trigger());
	}
}

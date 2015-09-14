package io.github.theangrydev.opper.recogniser.recursion;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;

public class PrecomputedRightRecursion implements RightRecursion {
	private final boolean[] rightRecursion;

	public PrecomputedRightRecursion(Grammar grammar, ComputedRightRecursion computedRightRecursion) {
		int rules = grammar.rules().size();
		this.rightRecursion = new boolean[rules];
		for (Rule rule : grammar.rules()) {
			rightRecursion[rule.id()] = computedRightRecursion.isRightRecursive(rule);
		}
	}

	@Override
	public boolean isRightRecursive(Rule rule) {
		return rightRecursion[rule.id()];
	}
}

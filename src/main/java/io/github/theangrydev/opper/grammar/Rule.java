package io.github.theangrydev.opper.grammar;

import java.util.function.Predicate;

public class Rule {

	private final int id;
	private final Symbol trigger;
	private final SymbolSequence derivation;

	public Rule(int id, Symbol trigger, SymbolSequence derivation) {
		this.id = id;
		this.trigger = trigger;
		this.derivation = derivation;
	}

	public static Predicate<Rule> triggeredBy(Symbol symbol) {
		return rule -> symbol == rule.trigger();
	}

	public int derivationLength() {
		return derivation.length();
	}

	public int derivationSuffixDotPosition() {
		return derivation.length() - 1;
	}

	public Symbol derivationSuffix() {
		return derivation(derivationSuffixDotPosition());
	}

	public boolean isRightRecursive() {
		return derivationSuffix() == trigger();
	}

	public Symbol derivation(int dotPosition) {
		return derivation.symbolAt(dotPosition);
	}

	public Symbol derivationPrefix() {
		return derivation(0);
	}

	public Symbol trigger() {
		return trigger;
	}

	public int id() {
		return id;
	}

	@Override
	public String toString() {
		return trigger + " -> " + derivation;
	}
}

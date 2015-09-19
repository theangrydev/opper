package io.github.theangrydev.opper.grammar;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;

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

	public List<Symbol> symbols() {
		return derivation.symbols();
	}

	public int derivationLength() {
		return derivation.length();
	}

	public int derivationSuffixDotPosition() {
		return derivation.lastIndexOf(trigger);
	}

	public Symbol derivationSuffix() {
		return derivation(derivationSuffixDotPosition());
	}

	public boolean isRightRecursive() {
		return derivationSuffixDotPosition() > 0 && derivationSuffix() == trigger();
	}

	public Symbol derivation(int dotPosition) {
		return derivation.symbolAt(dotPosition);
	}

	public List<Symbol> derivationPrefix() {
		return singletonList(derivation(0));
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

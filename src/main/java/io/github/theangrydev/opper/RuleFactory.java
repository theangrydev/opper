package io.github.theangrydev.opper;

public class RuleFactory {

	public Rule createRule(Symbol left, Symbol... right) {
		return new Rule(left, new SymbolSequence(right));
	}
}

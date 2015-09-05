package io.github.theangrydev.opper;

public class RuleFactory {

	public Rule createRule(Symbol left, Symbol... symbols) {
		return new Rule(left, new SymbolSequence(symbols));
	}
}

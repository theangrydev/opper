package io.github.theangrydev.opper;

public class RuleFactory {

	private int ruleId;

	public Rule createRule(Symbol left, Symbol... right) {
		return new Rule(ruleId++, left, new SymbolSequence(right));
	}
}

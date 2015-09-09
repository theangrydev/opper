package io.github.theangrydev.opper.grammar;

public class RuleFactory {

	private int ruleId;

	public Rule createRule(Symbol start, Symbol... derivation) {
		return new Rule(ruleId++, start, new SymbolSequence(derivation));
	}
}

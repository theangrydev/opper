package io.github.theangrydev.opper;

public class RuleFactory {

	private int idSequence;

	public Rule createRule(Symbol left, Symbol... symbols) {
		return new Rule(idSequence++, left, new SymbolSequence(symbols));
	}
}

package io.github.theangrydev.opper;

import java.util.List;
import java.util.Map;

public class SpecifiedGrammar implements Grammar {

	private final List<Symbol> symbols;
	private final Map<String, Symbol> symbolsByName;
	private final List<Rule> rules;
	private final Rule acceptanceRule;
	private final Symbol acceptanceSymbol;

	public SpecifiedGrammar(List<Symbol> symbols, Map<String, Symbol> symbolsByName, List<Rule> rules, Rule acceptanceRule, Symbol acceptanceSymbol) {
		this.symbols = symbols;
		this.symbolsByName = symbolsByName;
		this.rules = rules;
		this.acceptanceRule = acceptanceRule;
		this.acceptanceSymbol = acceptanceSymbol;
	}

	@Override
	public List<Symbol> symbols() {
		return symbols;
	}

	@Override
	public List<Rule> rules() {
		return rules;
	}

	@Override
	public Rule acceptanceRule() {
		return acceptanceRule;
	}

	@Override
	public Symbol acceptanceSymbol() {
		return acceptanceSymbol;
	}

	@Override
	public Symbol symbolByName(String name) {
		return symbolsByName.get(name);
	}
}

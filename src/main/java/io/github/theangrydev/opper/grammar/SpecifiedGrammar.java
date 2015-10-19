package io.github.theangrydev.opper.grammar;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SpecifiedGrammar implements Grammar {

	private final List<Symbol> symbols;
	private final Map<String, Symbol> symbolsByName;
	private final Map<List<String>, Rule> rulesByDefinition;
	private final List<Rule> rules;
	private final Rule acceptanceRule;
	private final Symbol acceptanceSymbol;
	private final Symbol emptySymbol;

	public SpecifiedGrammar(List<Symbol> symbols, Map<String, Symbol> symbolsByName, Map<List<String>, Rule> rulesByDefinition, List<Rule> rules, Rule acceptanceRule, Symbol acceptanceSymbol, Symbol emptySymbol) {
		this.symbols = symbols;
		this.symbolsByName = symbolsByName;
		this.rulesByDefinition = rulesByDefinition;
		this.rules = rules;
		this.acceptanceRule = acceptanceRule;
		this.acceptanceSymbol = acceptanceSymbol;
		this.emptySymbol = emptySymbol;
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

	@Override
	public Rule ruleByDefinition(String... definition) {
		return rulesByDefinition.get(Arrays.asList(definition));
	}

	@Override
	public Symbol emptySymbol() {
		return emptySymbol;
	}
}

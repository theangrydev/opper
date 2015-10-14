package io.github.theangrydev.opper.grammar;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GrammarBuilder {

	private final SymbolFactory symbolFactory;
	private final RuleFactory ruleFactory;

	private Map<String, Symbol> symbolsByName;
	private Map<String[], Rule> rulesByDefinition;
	private List<Symbol> symbols;
	private List<Rule> rules;
	private Symbol startSymbol;
	private Symbol acceptanceSymbol;

	public GrammarBuilder() {
		this.symbolFactory = new SymbolFactory();
		this.ruleFactory = new RuleFactory();
		this.symbols = new ObjectArrayList<>();
		this.rules = new ObjectArrayList<>();
		this.symbolsByName = new Object2ObjectArrayMap<>();
		this.rulesByDefinition = new Object2ObjectArrayMap<>();
	}

	public Grammar build() {
		Rule acceptanceRule = ruleFactory.createRule(acceptanceSymbol, startSymbol);
		rules.add(acceptanceRule);
		return new SpecifiedGrammar(symbols, symbolsByName, rules, acceptanceRule, acceptanceSymbol);
	}

	public GrammarBuilder withAcceptanceSymbol(String name) {
		acceptanceSymbol = createSymbol(name);
		return this;
	}

	public GrammarBuilder withStartSymbol(String name) {
		startSymbol = createSymbol(name);
		return this;
	}

	public GrammarBuilder withRule(String left, String... right) {
		Symbol leftSymbol = symbolByName(left);
		Symbol[] rightSymbols = Arrays.stream(right).map(this::symbolByName).toArray(Symbol[]::new);
		Rule rule = createRule(leftSymbol, rightSymbols);
		rules.add(rule);
		return this;
	}

	private Rule createRule(Symbol leftSymbol, Symbol[] rightSymbols) {
		Rule rule = ruleFactory.createRule(leftSymbol, rightSymbols);
		String[] definition = new String[rightSymbols.length + 1];
		definition[0] = leftSymbol.toString();
		for (int i = 0; i < rightSymbols.length; i++) {
			definition[i + 1] = rightSymbols[i].toString();
		}
		rulesByDefinition.put(definition, rule);
		return rule;
	}

	public Symbol symbolByName(String name) {
		Symbol symbol = symbolsByName.get(name);
		if (symbol == null) {
			return createSymbol(name);
		}
		return symbol;
	}

	public Rule ruleByDefinition(String... definition) {
		return rulesByDefinition.get(definition);
	}

	private Symbol createSymbol(String name) {
		Symbol symbol = symbolFactory.createSymbol(name);
		symbols.add(symbol);
		symbolsByName.put(name, symbol);
		return symbol;
	}
}

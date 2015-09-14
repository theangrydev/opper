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

	public GrammarBuilder withSymbol(String name) {
		createSymbol(name);
		return this;
	}

	public GrammarBuilder withSymbols(String... names) {
		for (String name : names) {
			withSymbol(name);
		}
		return this;
	}

	public GrammarBuilder withRule(String left, String... right) {
		Symbol leftSymbol = symbolByName(left);
		Symbol[] rightSymbols = Arrays.stream(right).map(this::symbolByName).toArray(Symbol[]::new);
		Rule rule = ruleFactory.createRule(leftSymbol, rightSymbols);
		rules.add(rule);
		return this;
	}

	private Symbol symbolByName(String name) {
		Symbol symbol = symbolsByName.get(name);
		if (symbol == null) {
			throw new IllegalArgumentException("'" + name + "' is not a known symbol");
		}
		return symbol;
	}

	private Symbol createSymbol(String name) {
		Symbol symbol = symbolFactory.createSymbol(name);
		symbols.add(symbol);
		symbolsByName.put(name, symbol);
		return symbol;
	}
}

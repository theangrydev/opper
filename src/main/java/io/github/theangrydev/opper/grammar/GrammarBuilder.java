package io.github.theangrydev.opper.grammar;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GrammarBuilder {

	private final SymbolFactory symbolFactory;
	private final RuleFactory ruleFactory;

	private Map<String, Symbol> symbolsByName;
	private Map<List<String>, Rule> rulesByDefinition;
	private List<Symbol> symbols;
	private List<Rule> rules;
	private Symbol startSymbol;
	private Symbol acceptanceSymbol;
	private Symbol emptySymbol;

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
		if (emptySymbol == null) {
			withEmptySymbol("");
		}
		return new SpecifiedGrammar(symbols, symbolsByName, rulesByDefinition, rules, acceptanceRule, acceptanceSymbol, emptySymbol);
	}

	public GrammarBuilder withEmptySymbol(String name) {
		emptySymbol = createSymbol(name);
		return this;
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
		List<String> definition = new ArrayList<>(rightSymbols.length + 1);
		definition.add(leftSymbol.toString());
		for (Symbol rightSymbol : rightSymbols) {
			definition.add(rightSymbol.toString());
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
		Rule rule = rulesByDefinition.get(Arrays.asList(definition));
		if (rule == null) {
			throw new IllegalStateException("Could not find rule defined by " + Arrays.toString(definition));
		}
		return rule;
	}

	private Symbol createSymbol(String name) {
		Symbol symbol = symbolFactory.createSymbol(name);
		symbols.add(symbol);
		symbolsByName.put(name, symbol);
		return symbol;
	}
}

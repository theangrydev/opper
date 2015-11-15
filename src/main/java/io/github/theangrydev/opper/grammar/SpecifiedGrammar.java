/**
 * Copyright 2015 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.grammar;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

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
	public List<Symbol> symbolsByName(String... names) {
		return stream(names).map(this::symbolByName).collect(toList());
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

/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
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
package io.github.theangrydev.opper.parser.precomputed.nullable;

import com.google.common.base.Preconditions;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.tree.ParseTree;
import io.github.theangrydev.opper.parser.tree.ParseTreeNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.theangrydev.opper.common.Predicates.not;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class DirectNullableSymbolParseTreeComputer implements NullableSymbolParseTreeComputer {

	private final Grammar grammar;
	private final NullableSymbolParseTreeComputer computer;
	private final Map<Symbol, List<Rule>> rulesWithTrigger;

	public DirectNullableSymbolParseTreeComputer(Grammar grammar, NullableSymbolParseTreeComputer computer) {
		this.grammar = grammar;
		this.computer = computer;
		this.rulesWithTrigger = grammar.rules().stream().collect(groupingBy(Rule::trigger));
	}

	@Override
	public Optional<ParseTree> nullParseTree(Symbol symbol) {
		List<ParseTree> nullParseTrees = rulesWithTrigger.getOrDefault(symbol, emptyList()).stream().map(this::nullParseTree).filter(Optional::isPresent).map(Optional::get).collect(toList());
		Preconditions.checkState(nullParseTrees.size() <= 1, "Found more than one null parse tree for symbol '%s': '%s'", symbol, nullParseTrees);
		if (nullParseTrees.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(nullParseTrees.get(0));
	}

	private Optional<ParseTree> nullParseTree(Rule rule) {
		if (derivationIsDirectlyEmpty(rule)) {
			return Optional.of(ParseTreeNode.node(rule));
		}
		List<Optional<ParseTree>> derivation = rule.derivation().stream().map(computer::nullParseTree).collect(toList());
		if (derivation.stream().anyMatch(not(Optional::isPresent))) {
			return Optional.empty();
		}
		ParseTreeNode node = ParseTreeNode.node(rule);
		derivation.stream().map(Optional::get).forEach(node::withChild);
		return Optional.of(node);
	}

	private boolean derivationIsDirectlyEmpty(Rule rule) {
		return rule.derivation().stream().allMatch(isEqual(grammar.emptySymbol()));
	}
}

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

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.tree.ParseTree;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.List;
import java.util.Optional;

public class CachingNullableSymbolParseTreeComputer implements NullableSymbolParseTreeComputer {

	private final ObjectList<NullableRuleCheck> nullableRuleChecks;
	private final NullableSymbolParseTreeComputer computer;

	public CachingNullableSymbolParseTreeComputer(Grammar grammar) {
		this.computer = new DirectNullableSymbolParseTreeComputer(grammar, this);
		List<Symbol> symbols = grammar.symbols();
		this.nullableRuleChecks = new ObjectArrayList<>(symbols.size());
		nullableRuleChecks.size(symbols.size());
		for (Symbol symbol : grammar.symbols()) {
			nullableRuleChecks.set(symbol.id(), NullableRuleCheck.nullCheck(symbol));
		}
	}

	@Override
	public Optional<ParseTree> nullParseTree(Symbol symbol) {
		NullableRuleCheck nullableRuleCheck = nullableRuleChecks.get(symbol.id());
		if (nullableRuleCheck.hasBeenChecked()) {
			return nullableRuleCheck.nullParseTree();
		}
		if (nullableRuleCheck.isChecking()) {
			// this situation implies a recursive rule, which would have an infinite parse tree, so we do not consider the symbol to be nullable
			return Optional.empty();
		}
		nullableRuleCheck.startChecking();
		Optional<ParseTree> nullParseTree = computer.nullParseTree(symbol);
		nullableRuleCheck.recordNullParseTree(nullParseTree);
		return nullParseTree;
	}

	private static class NullableRuleCheck {
		private boolean isChecking;
		private boolean hasBeenChecked;
		private Optional<ParseTree> nullParseTree;
		private final Symbol symbol;

		private NullableRuleCheck(Symbol symbol) {
			this.symbol = symbol;
		}

		public static NullableRuleCheck nullCheck(Symbol rule) {
			return new NullableRuleCheck(rule);
		}

		public boolean hasBeenChecked() {
			return hasBeenChecked;
		}

		public Optional<ParseTree> nullParseTree() {
			return nullParseTree;
		}

		public Symbol symbol() {
			return symbol;
		}

		public void recordNullParseTree(Optional<ParseTree> isNullable) {
			this.nullParseTree = isNullable;
			hasBeenChecked = true;
			isChecking = false;
		}

		public void startChecking() {
			isChecking = true;
		}

		public boolean isChecking() {
			return isChecking;
		}
	}
}

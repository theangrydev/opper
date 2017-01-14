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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.theangrydev.opper.common.Predicates.not;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class NullableSymbolParseTreeComputer {

    private final Grammar grammar;
    private final Map<Symbol, List<Rule>> rulesWithTrigger;
    private final ObjectList<NullableRuleCheck> nullableRuleChecks;

    public NullableSymbolParseTreeComputer(Grammar grammar) {
        this.grammar = grammar;
        List<Symbol> symbols = grammar.symbols();
        this.nullableRuleChecks = new ObjectArrayList<>(symbols.size());
        nullableRuleChecks.size(symbols.size());
        for (Symbol symbol : grammar.symbols()) {
            nullableRuleChecks.set(symbol.id(), NullableRuleCheck.nullCheck(symbol));
        }
        this.rulesWithTrigger = grammar.rules().stream().collect(groupingBy(Rule::trigger));
    }

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
        Optional<ParseTree> nullParseTree = directNullParseTree(symbol);
        nullableRuleCheck.recordNullParseTree(nullParseTree);
        return nullParseTree;
    }

    private Optional<ParseTree> directNullParseTree(Symbol symbol) {
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
        List<Optional<ParseTree>> derivation = rule.derivation().stream().map(this::nullParseTree).collect(toList());
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

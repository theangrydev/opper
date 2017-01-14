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
package io.github.theangrydev.opper.parser.precomputed.prediction;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.early.DottedRule;
import io.github.theangrydev.opper.parser.precomputed.DerivationConsequences;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class ComputedRulePrediction implements RulePrediction {

    private final DottedRuleFactory dottedRuleFactory;
    private final DerivationConsequences derivationPrefixes;
    private final Grammar grammar;

    private ComputedRulePrediction(Grammar grammar, DottedRuleFactory dottedRuleFactory, DerivationConsequences derivationPrefixes) {
        this.grammar = grammar;
        this.dottedRuleFactory = dottedRuleFactory;
        this.derivationPrefixes = derivationPrefixes;
    }

    public static ComputedRulePrediction computedRulePrediction(Grammar grammar) {
        DottedRuleFactory dottedRuleFactory = new DottedRuleFactory(grammar);
        DerivationConsequences derivationPrefixes = new DerivationConsequences(grammar, Rule::derivationPrefix);
        return new ComputedRulePrediction(grammar, dottedRuleFactory, derivationPrefixes);
    }

    @Override
    public List<DottedRule> rulesThatCanBeTriggeredBy(Symbol symbol) {
        return rulesTriggeredBy(derivationPrefixes.of(symbol));
    }

    @Override
    public DottedRule initial() {
        return dottedRuleFactory.begin(grammar.acceptanceRule());
    }

    private List<DottedRule> rulesTriggeredBy(Set<Symbol> symbols) {
        return grammar.rules().stream().filter(triggeredByOneOf(symbols)).map(dottedRuleFactory::begin).collect(toList());
    }

    private Predicate<Rule> triggeredByOneOf(Set<Symbol> symbols) {
        return rule -> symbols.contains(rule.trigger());
    }
}

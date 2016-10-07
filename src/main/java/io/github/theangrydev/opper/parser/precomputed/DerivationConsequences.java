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
package io.github.theangrydev.opper.parser.precomputed;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.github.theangrydev.opper.grammar.Rule.triggeredBy;

public class DerivationConsequences {

	private final Function<Rule, Symbol> consequence;
	private final Grammar grammar;

	public DerivationConsequences(Grammar grammar, Function<Rule, Symbol> consequence) {
		this.consequence = consequence;
		this.grammar = grammar;
	}

	public Set<Symbol> of(Symbol symbol) {
		List<Symbol> confirmedConsequences = new ObjectArrayList<>();
		confirmedConsequences.add(symbol);

		Set<Symbol> uniqueConsequences = new ObjectArraySet<>();
		uniqueConsequences.add(symbol);

		confirmedConsequences.forEach(confirmedConsequence -> rulesTriggeredBy(confirmedConsequence).map(consequence).forEach(consequence -> {
			boolean wasNew = uniqueConsequences.add(consequence);
			if (wasNew) {
				confirmedConsequences.add(consequence);
			}
		}));
		return uniqueConsequences;
	}

	private Stream<Rule> rulesTriggeredBy(Symbol symbol) {
		return grammar.rules().stream().filter(triggeredBy(symbol));
	}
}

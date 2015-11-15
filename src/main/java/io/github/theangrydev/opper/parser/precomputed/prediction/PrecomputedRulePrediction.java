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
package io.github.theangrydev.opper.parser.precomputed.prediction;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.early.DottedRule;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.List;

public class PrecomputedRulePrediction implements RulePrediction {

	private final DottedRule initial;
	private final ObjectList<List<DottedRule>> predictions;

	public PrecomputedRulePrediction(Grammar grammar, ComputedRulePrediction computedRulePrediction) {
		int symbols = grammar.symbols().size();
		this.predictions = new ObjectArrayList<>(symbols);
		predictions.size(symbols);
		for (Symbol symbol : grammar.symbols()) {
			predictions.set(symbol.id(), computedRulePrediction.rulesThatCanBeTriggeredBy(symbol));
		}
		this.initial = computedRulePrediction.initial();
	}

	@Override
	public List<DottedRule> rulesThatCanBeTriggeredBy(Symbol startSymbol) {
		return predictions.get(startSymbol.id());
	}

	@Override
	public DottedRule initial() {
		return initial;
	}
}

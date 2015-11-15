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
package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.parser.precomputed.nullable.CachingNullableRuleComputer;
import io.github.theangrydev.opper.parser.precomputed.nullable.NullableSymbolComputer;
import io.github.theangrydev.opper.parser.precomputed.nullable.NullableSymbols;
import io.github.theangrydev.opper.parser.precomputed.prediction.ComputedRulePrediction;
import io.github.theangrydev.opper.parser.precomputed.prediction.PrecomputedRulePrediction;
import io.github.theangrydev.opper.parser.precomputed.recursion.ComputedRightRecursion;
import io.github.theangrydev.opper.parser.precomputed.recursion.PrecomputedRightRecursion;
import io.github.theangrydev.opper.scanner.Scanner;

public class EarlyParserFactory implements ParserFactory {

	private final Logger logger;
	private final Grammar grammar;
	private final PrecomputedRightRecursion rightRecursion;
	private final PrecomputedRulePrediction rulePrediction;
	private final NullableSymbols nullableSymbols;

	public EarlyParserFactory(Logger logger, Grammar grammar) {
		this.logger = logger;
		this.grammar = grammar;
		this.rightRecursion = new PrecomputedRightRecursion(grammar, new ComputedRightRecursion(grammar));
		this.rulePrediction = new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(grammar));
		this.nullableSymbols = new NullableSymbols(grammar, new NullableSymbolComputer(grammar, new CachingNullableRuleComputer(grammar, rulePrediction)));
	}

	@Override
	public EarlyParser parser(Scanner scanner) {
		return new EarlyParser(logger, grammar, rightRecursion, rulePrediction, nullableSymbols, scanner);
	}
}

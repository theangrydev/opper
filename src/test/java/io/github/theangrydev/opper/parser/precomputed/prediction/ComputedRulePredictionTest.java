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

import com.googlecode.yatspec.junit.Row;
import com.googlecode.yatspec.junit.Table;
import com.googlecode.yatspec.junit.TableRunner;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.parser.early.DottedRule;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(TableRunner.class)
public class ComputedRulePredictionTest implements WithAssertions {

	@Table({
		@Row({"LIST",
			"LIST -> . THING",
			"LIST -> . LIST , THING",
			"THING -> . TREE",
			"THING -> . NAME",
			"TREE -> . ( LIST )",
			"NAME -> . ant",
			"NAME -> . bat",
			"NAME -> . cow"
		}),
		@Row({"THING",
			"THING -> . TREE",
			"THING -> . NAME",
			"TREE -> . ( LIST )",
			"NAME -> . ant",
			"NAME -> . bat",
			"NAME -> . cow"}),
		@Row({"NAME",
			"NAME -> . ant",
			"NAME -> . bat",
			"NAME -> . cow"}),
		@Row({"TREE",
			"TREE -> . ( LIST )"
		}),
		@Row({"ant"}),
		@Row({"bat"}),
		@Row({"cow"})
	})
	@Test
	public void symbolsPredictRules(String symbol, String... predictedRules) {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("TREE")
			.withRule("TREE", "(", "LIST", ")")
			.withRule("LIST", "THING")
			.withRule("LIST", "LIST", ",", "THING")
			.withRule("THING", "TREE")
			.withRule("THING", "NAME")
			.withRule("NAME", "ant")
			.withRule("NAME", "bat")
			.withRule("NAME", "cow")
			.build();

		ComputedRulePrediction prediction = new ComputedRulePrediction(grammar);

		List<DottedRule> predictions = prediction.rulesThatCanBeTriggeredBy(grammar.symbolByName(symbol));

		assertThat(predictions).extracting(String::valueOf).containsOnly(predictedRules);
	}
}

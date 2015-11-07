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

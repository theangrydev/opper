package io.github.theangrydev.opper.parser.precomputed.nullable;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.parser.precomputed.prediction.ComputedRulePrediction;
import io.github.theangrydev.opper.parser.precomputed.prediction.PrecomputedRulePrediction;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class NullableSymbolComputerTest implements WithAssertions {

	@Test
	public void computesNullableSymbolsForGrammarWithNullableSymbols() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("TREE")
			.withEmptySymbol("NONE")
			.withRule("A", "B", "C")
			.withRule("A", "D", "C")
			.withRule("A'", "C")
			.withRule("C", "NONE")
			.withRule("D", "SOME")
			.build();

		NullableSymbolComputer computer = new NullableSymbolComputer(grammar, new CachingNullableRuleComputer(grammar, new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(grammar))));

		assertThat(computer.computeNullableSymbols()).containsOnlyElementsOf(grammar.symbolsByName("A'", "C"));
	}

	@Test
	public void computesNullableSymbolsForRightRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("START")
			.withEmptySymbol("NONE")
			.withRule("START", "REPEATED", "START")
			.withRule("START", "REPEATED")
			.withRule("REPEATED", "NONE")
			.withRule("REPEATED", "SOME")
			.build();

		NullableSymbolComputer computer = new NullableSymbolComputer(grammar, new CachingNullableRuleComputer(grammar, new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(grammar))));

		assertThat(computer.computeNullableSymbols()).containsOnlyElementsOf(grammar.symbolsByName("REPEATED"));
	}
}

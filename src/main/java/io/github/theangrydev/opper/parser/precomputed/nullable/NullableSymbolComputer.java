package io.github.theangrydev.opper.parser.precomputed.nullable;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.grammar.Symbol;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class NullableSymbolComputer {

	private final Grammar grammar;
	private final NullableRuleComputer nullableRuleComputer;

	public NullableSymbolComputer(Grammar grammar, NullableRuleComputer nullableRuleComputer) {
		this.grammar = grammar;
		this.nullableRuleComputer = nullableRuleComputer;
	}

	public Set<Symbol> computeNullableSymbols() {
		return grammar.rules().stream().filter(nullableRuleComputer::isNullable).map(Rule::trigger).collect(toSet());
	}
}

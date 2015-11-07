package io.github.theangrydev.opper.parser.precomputed.nullable;

import io.github.theangrydev.opper.grammar.Rule;

public interface NullableRuleComputer {
	boolean isNullable(Rule rule);
}

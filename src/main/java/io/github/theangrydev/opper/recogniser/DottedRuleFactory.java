package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Rule;

public class DottedRuleFactory {

	private final DottedRule[] dottedRules;

	public DottedRuleFactory(Grammar grammar) {
		this.dottedRules = new DottedRule[grammar.rules().size()];
		for (Rule rule : grammar.rules()) {
			dottedRules[rule.id()] = DottedRule.begin(rule);
		}
	}

	public DottedRule begin(Rule rule) {
		return dottedRules[rule.id()];
	}
}

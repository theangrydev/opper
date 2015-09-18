package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.grammar.Rule;

public class EarlyItemFactory {

	private final DottedRuleFactory dottedRuleFactory;

	public EarlyItemFactory(DottedRuleFactory dottedRuleFactory) {
		this.dottedRuleFactory = dottedRuleFactory;
	}

	public EarlyItem createEarlyItem(DottedRule dottedRule, int origin) {
		return new EarlyItem(dottedRule, origin);
	}

	public EarlyItem createEarlyItem(Rule rule, int origin) {
		return createEarlyItem(dottedRuleFactory.begin(rule), origin);
	}
}

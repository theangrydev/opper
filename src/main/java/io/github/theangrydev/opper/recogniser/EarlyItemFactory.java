package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Rule;

public class EarlyItemFactory {

	public EarlyItem createEarlyItem(DottedRule dottedRule, int origin) {
		return new EarlyItem(dottedRule, origin);
	}

	public EarlyItem createEarlyItem(Rule rule, int origin) {
		return createEarlyItem(DottedRule.begin(rule), origin);
	}
}

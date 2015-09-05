package io.github.theangrydev.opper;

import java.util.Arrays;
import java.util.List;

public class ExampleGrammar implements Grammar {
	public static final Symbol DUMMY = new Symbol(4, "DUMMY");
	public static final Symbol SECOND = new Symbol(3, "SECOND");
	public static final Symbol ACCEPT = new Symbol(1, "ACCEPT");
	public static final Symbol FIRST = new Symbol(2, "FIRST");
	public static final Symbol START = new Symbol(0, "START");

	public static final Rule ACCEPT_RULE = new Rule(0, ACCEPT, new String(SECOND, DUMMY));
	public static final Rule FIRST_TO_ACCEPT = new Rule(2, FIRST, new String(ACCEPT));
	public static final Rule START_TO_FIRST = new Rule(1, START, new String(FIRST));

	@Override
	public List<Symbol> symbols() {
		return Arrays.asList(START, ACCEPT, FIRST, SECOND, DUMMY);
	}

	@Override
	public List<Rule> rules() {
		return Arrays.asList(ACCEPT_RULE, FIRST_TO_ACCEPT);
	}

	@Override
	public Symbol acceptanceSymbol() {
		return ACCEPT;
	}

	@Override
	public Rule startRule() {
		return START_TO_FIRST;
	}
}

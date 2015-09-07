package io.github.theangrydev.opper;

import java.util.List;

public interface Grammar {
	List<Symbol> symbols();
	List<Rule> rules();
	Rule acceptanceRule();
	Symbol acceptanceSymbol();
	Symbol symbolByName(String name);
}

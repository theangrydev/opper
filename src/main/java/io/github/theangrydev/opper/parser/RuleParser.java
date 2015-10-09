package io.github.theangrydev.opper.parser;

import java.util.List;

@FunctionalInterface
public interface RuleParser {
	Object parse(List<Object> arguments);
}

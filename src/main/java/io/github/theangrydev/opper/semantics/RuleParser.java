package io.github.theangrydev.opper.semantics;

import java.util.List;

@FunctionalInterface
public interface RuleParser {
	Object parse(List<Object> arguments);
}

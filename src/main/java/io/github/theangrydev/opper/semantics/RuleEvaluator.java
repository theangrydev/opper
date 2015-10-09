package io.github.theangrydev.opper.semantics;

import java.util.List;

@FunctionalInterface
public interface RuleEvaluator {
	Object parse(List<Object> arguments);
}

package io.github.theangrydev.opper.semantics;

import java.util.List;

@FunctionalInterface
public interface RuleEvaluator {
	Object evaluate(List<Object> arguments);
}

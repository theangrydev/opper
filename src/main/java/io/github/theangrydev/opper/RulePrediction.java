package io.github.theangrydev.opper;

import java.util.List;

public interface RulePrediction {
	List<Rule> predict(Symbol symbol);
}

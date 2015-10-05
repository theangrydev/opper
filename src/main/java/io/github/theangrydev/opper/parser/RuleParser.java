package io.github.theangrydev.opper.parser;

import java.util.List;

public interface RuleParser {
	Object parse(String content, List<Object> arguments);
}

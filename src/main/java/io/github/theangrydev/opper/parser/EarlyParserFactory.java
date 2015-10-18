package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.Logger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.parser.precomputed.prediction.ComputedRulePrediction;
import io.github.theangrydev.opper.parser.precomputed.prediction.PrecomputedRulePrediction;
import io.github.theangrydev.opper.parser.precomputed.recursion.ComputedRightRecursion;
import io.github.theangrydev.opper.parser.precomputed.recursion.PrecomputedRightRecursion;
import io.github.theangrydev.opper.scanner.Scanner;

public class EarlyParserFactory implements ParserFactory {

	private final Logger logger;
	private final Grammar grammar;
	private final PrecomputedRightRecursion rightRecursion;
	private final PrecomputedRulePrediction rulePrediction;

	public EarlyParserFactory(Logger logger, Grammar grammar) {
		this.logger = logger;
		this.grammar = grammar;
		this.rightRecursion = new PrecomputedRightRecursion(grammar, new ComputedRightRecursion(grammar));
		this.rulePrediction = new PrecomputedRulePrediction(grammar, new ComputedRulePrediction(grammar));
	}

	@Override
	public EarlyParser parser(Scanner scanner) {
		return new EarlyParser(logger, grammar, rightRecursion, rulePrediction, scanner);
	}
}

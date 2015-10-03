package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.SymbolOwnedStateGenerator;

import java.util.Arrays;

public class ConcatenateExpression implements Expression {

	private final Expression[] expressions;

	private ConcatenateExpression(Expression... expressions) {
		this.expressions = expressions;
	}

	public static ConcatenateExpression concatenate(Expression... expressions) {
		return new ConcatenateExpression(expressions);
	}

	@Override
	public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
		for (int i = 0; i < expressions.length - 1; i++) {
			Expression left = expressions[i];
			Expression right = expressions[i + 1];
			State middle = generator.newState();
			left.populate(generator, from, middle);
			right.populate(generator, middle, to);
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(expressions);
	}
}

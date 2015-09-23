package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.SymbolOwnedStateGenerator;

public class ConcatenateExpression implements Expression {

	private final Expression left;
	private final Expression right;

	private ConcatenateExpression(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}

	public static ConcatenateExpression concatenate(Expression left, Expression right) {
		return new ConcatenateExpression(left, right);
	}

	@Override
	public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
		State middle = generator.newState();
		left.populate(generator, from, middle);
		right.populate(generator, middle, to);
	}

	@Override
	public String toString() {
		return left.toString() + right.toString();
	}
}

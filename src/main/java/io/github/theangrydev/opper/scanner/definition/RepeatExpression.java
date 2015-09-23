package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.SymbolOwnedStateGenerator;

public class RepeatExpression implements Expression {

	private final Expression expressionToRepeat;

	private RepeatExpression(Expression expressionToRepeat) {
		this.expressionToRepeat = expressionToRepeat;
	}

	public static RepeatExpression repeat(Expression expressionToRepeat) {
		return new RepeatExpression(expressionToRepeat);
	}

	@Override
	public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
		from.addNullTransition(to);

		State repeatStart = generator.newState();
		from.addNullTransition(repeatStart);

		State repeatEnd = generator.newState();
		repeatEnd.addNullTransition(to);
		repeatEnd.addNullTransition(repeatStart);

		expressionToRepeat.populate(generator, repeatStart, repeatEnd);
	}

	@Override
	public String toString() {
		return expressionToRepeat + "*";
	}
}

package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.automaton.nfa.SymbolOwnedStateGenerator;

import java.util.Arrays;
import java.util.List;

public class AlternativeExpression implements Expression {

	private final List<Expression> alternatives;

	private AlternativeExpression(List<Expression> alternatives) {
		this.alternatives = alternatives;
	}

	public static AlternativeExpression either(Expression... alternatives) {
		return new AlternativeExpression(Arrays.asList(alternatives));
	}

	@Override
	public void populate(SymbolOwnedStateGenerator generator, State from, State to) {
		for (Expression alternative : alternatives) {
			State alternativeFrom = generator.newState();
			from.addNullTransition(alternativeFrom);

			State alternativeTo = generator.newState();
			alternativeTo.addNullTransition(to);

			alternative.populate(generator, alternativeFrom, alternativeTo);
		}
	}

	@Override
	public String toString() {
		return alternatives.toString();
	}
}

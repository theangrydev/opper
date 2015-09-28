package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.StateFactory;
import io.github.theangrydev.opper.scanner.autonoma.SymbolOwnedStateGenerator;
import io.github.theangrydev.opper.scanner.autonoma.TransitionFactory;
import org.junit.Test;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;

public class ExpressionsCanBeConstructedTest {

	@Test
	public void shouldHaveAGoodStringRepresentation() {
		StateFactory stateFactory = new StateFactory();
		TransitionFactory transitionFactory = new TransitionFactory();
		State from = stateFactory.anonymousState();
		State to = stateFactory.anonymousState();

		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));

		expression.populate(new SymbolOwnedStateGenerator(new Symbol(1, "symbol"), stateFactory, transitionFactory), from, to);

		System.out.println(expression);
	}
}

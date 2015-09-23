package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.State;
import io.github.theangrydev.opper.scanner.autonoma.StateFactory;
import io.github.theangrydev.opper.scanner.autonoma.SymbolOwnedStateGenerator;
import org.junit.Test;

import java.util.stream.Collectors;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;

public class StatesCanBeConstructedUsingExpressionsTest {

	@Test
	public void shouldConstructStatesFromAnExpression() {
		StateFactory stateFactory = new StateFactory();
		State from = stateFactory.anonymousState();
		State to = stateFactory.anonymousState();

		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));

		expression.populate(new SymbolOwnedStateGenerator(new Symbol(1, "symbol"), stateFactory), from, to);

		System.out.println(stateFactory.states().stream().map(Object::toString).collect(Collectors.joining("\n")));
	}
}

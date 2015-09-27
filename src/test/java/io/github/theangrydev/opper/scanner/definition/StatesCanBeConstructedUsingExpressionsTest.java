package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.*;
import org.junit.Test;

import java.util.Collections;
import java.util.stream.Collectors;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;

public class StatesCanBeConstructedUsingExpressionsTest {

	@Test
	public void shouldConstructStatesFromAnExpression() {
		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));
		SymbolDefinition symbolDefinition = new SymbolDefinition(new Symbol(1, "symbol"), expression);

		StateFactory stateFactory = new StateFactory();
		SymbolDefinitionToStateConverter converter = new SymbolDefinitionToStateConverter(stateFactory);

		State initial = converter.convertDefinitionsToStates(Collections.singletonList(symbolDefinition));

		stateFactory.eliminateEpsilonTransitions();
		initial.markReachableStates();
		stateFactory.removeUnreachableStates();

		StateStatistics stateStatistics = new StateStatistics();
		stateFactory.states().forEach(stateStatistics::record);

		StateEncoder stateEncoder = new StateEncoder();
		stateEncoder.labelStatesWithSmallerIdsForMoreFrequentStates(stateStatistics);

		System.out.println(stateFactory.states().stream().map(Object::toString).collect(Collectors.joining("\n")));
		System.out.println(stateStatistics);
	}
}

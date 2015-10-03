package io.github.theangrydev.opper.scanner.definition;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;

public class ExpressionsCanBeConstructedTest implements WithAssertions {

	@Test
	public void shouldHaveAGoodStringRepresentation() {
		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));
		assertThat(expression).hasToString("[a, b, [c, d]]*");
	}
}

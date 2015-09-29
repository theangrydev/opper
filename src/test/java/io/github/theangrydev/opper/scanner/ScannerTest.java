package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.definition.Expression;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;
import static java.util.Collections.singletonList;

public class ScannerTest implements WithAssertions {

	@Test
	public void shouldScanASimpleExpression() {
		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));
		Symbol symbol = new Symbol(1, "symbol");
		SymbolDefinition symbolDefinition = new SymbolDefinition(symbol, expression);

		Scanner scanner = new Scanner(singletonList(symbolDefinition), 'c', 'd', 'c', 'd', 'a', 'b');

		assertThat(allSymbolsThatCanBeScanned(scanner)).containsOnly(symbol).hasSize(4);
	}

	private List<Symbol> allSymbolsThatCanBeScanned(Scanner scanner) {
		List<Symbol> accepted = new ArrayList<>();
		while (scanner.hasNextSymbol()) {
			accepted.add(scanner.nextSymbol());
		}
		return accepted;
	}
}

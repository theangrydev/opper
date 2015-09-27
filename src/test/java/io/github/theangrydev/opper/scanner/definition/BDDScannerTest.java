package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.bdd.BDDScanner;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;
import static java.util.Collections.singletonList;

public class BDDScannerTest implements WithAssertions {

	@Test
	public void shouldScanASimpleExpression() {
		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));
		Symbol symbol = new Symbol(1, "symbol");
		SymbolDefinition symbolDefinition = new SymbolDefinition(symbol, expression);

		BDDScanner bddScanner = new BDDScanner(singletonList(symbolDefinition), 'c', 'd', 'c', 'd', 'a', 'b');

		assertThat(allSymbolsThatCanBeScanned(bddScanner)).containsOnly(symbol).hasSize(4);
	}

	private List<Symbol> allSymbolsThatCanBeScanned(BDDScanner bddScanner) {
		List<Symbol> accepted = new ArrayList<>();
		while (bddScanner.hasNextSymbol()) {
			accepted.add(bddScanner.nextSymbol());
		}
		return accepted;
	}
}

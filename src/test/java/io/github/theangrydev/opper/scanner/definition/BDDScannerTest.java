package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.BDDScanner;
import org.junit.Test;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;
import static java.util.Collections.singletonList;

public class BDDScannerTest {

	@Test
	public void shouldScan() {
		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));
		SymbolDefinition symbolDefinition = new SymbolDefinition(new Symbol(1, "symbol"), expression);

		//TODO: this is broken for repeated inputs and other stuff
		BDDScanner bddScanner = new BDDScanner(singletonList(symbolDefinition), 'c', 'd', 'c', 'd');
		while (bddScanner.hasNextSymbol()) {
			System.out.println("next=" + bddScanner.nextSymbol());
		}
	}
}

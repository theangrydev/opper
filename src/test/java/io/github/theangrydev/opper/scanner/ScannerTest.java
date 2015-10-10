package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;
import static io.github.theangrydev.opper.scanner.definition.SymbolDefinition.definition;
import static java.util.Arrays.asList;

public class ScannerTest implements WithAssertions {

	@Test
	public void shouldScanASimpleExpression() {
		Symbol a = new Symbol(1, "a");
		SymbolDefinition aDefinition = definition(a, repeat(either(character('a'), character('b'), concatenate(character('c'), character('d')))));
		Symbol b = new Symbol(2, "b");
		SymbolDefinition bDefinition = definition(b, repeat(either(character('0'), character('1'))));

		Scanner scanner = new BFAScanner(asList(aDefinition, bDefinition), new CharArrayReader(new char[]{'c', 'd', '0', '1', 'c', 'd', 'a', 'b'}));

		assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(a, b, b, a, a, a);
	}

	private List<Symbol> allSymbolsThatCanBeScanned(Scanner scanner) {
		List<Symbol> accepted = new ArrayList<>();
		while (scanner.hasNextSymbol()) {
			accepted.add(scanner.nextSymbol().symbol());
		}
		return accepted;
	}
}

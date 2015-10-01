package io.github.theangrydev.opper.performance;

import com.google.common.base.Stopwatch;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.Scanner;
import io.github.theangrydev.opper.scanner.definition.Expression;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.Arrays;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ScannerPerformanceTest implements WithAssertions {

	@Test
	public void shouldScanASimpleExpression() {
		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));
		Symbol symbol = new Symbol(1, "symbol");
		SymbolDefinition symbolDefinition = new SymbolDefinition(symbol, expression);

		Scanner scanner = new Scanner(singletonList(symbolDefinition), characters(10000));

		Stopwatch stopwatch = Stopwatch.createStarted();

		scanAllSymbols(scanner);

		assertThat(stopwatch.elapsed(MILLISECONDS)).describedAs("Time taken should be less than 100ms").isLessThan(100);
	}

	private char[] characters(int numberOfCharacters) {
		char[] chars = new char[numberOfCharacters];
		Arrays.fill(chars, 'a');
		return chars;
	}

	private void scanAllSymbols(Scanner scanner) {
		while (scanner.hasNextSymbol()) {
			scanner.nextSymbol();
		}
	}
}

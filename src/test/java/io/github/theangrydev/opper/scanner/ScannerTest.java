package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.scanner.Location.location;
import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;
import static io.github.theangrydev.opper.scanner.definition.SymbolDefinition.definition;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ScannerTest implements WithAssertions {

	@Test
	public void shouldScanASimpleExpression() {
		Symbol a = new Symbol(1, "a");
		SymbolDefinition aDefinition = definition(a, repeat(either(character('a'), character('b'), concatenate(character('c'), character('d')))));
		Symbol b = new Symbol(2, "b");
		SymbolDefinition bDefinition = definition(b, repeat(either(character('0'), character('1'))));

		Scanner scanner = new BFAScanner(asList(aDefinition, bDefinition), new StringReader("cd01cdab"));

		assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(a, b, a);
	}

	@Test
	public void shouldNotBlowUpWhenAnUnsupportedCharacterIsScanned() {
		Symbol a = new Symbol(1, "a");
		SymbolDefinition aDefinition = definition(a, character('a'));

		Scanner scanner = new BFAScanner(singletonList(aDefinition), new StringReader("zzazzazzz"));
		assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(a, a);
	}

	@Test
	public void shouldRecordLocationInformation1() {
		Symbol a = new Symbol(1, "a");
		SymbolDefinition aDefinition = definition(a, either(character('a'), concatenate(character('b'), character('c'))));

		Scanner scanner = new BFAScanner(singletonList(aDefinition), new StringReader("a\nbc\nabcd"));
		assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(a, a, a, a);
	}

	@Test
	public void shouldRecordLocationInformation() {
		Symbol a = new Symbol(1, "a");
		SymbolDefinition aDefinition = definition(a, either(character('a'), concatenate(character('b'), character('c'))));

		Scanner scanner = new BFAScanner(singletonList(aDefinition), new StringReader("a\nbc\nabcd"));
		assertThat(allLocationsThatCanBeScanned(scanner)).containsExactly(
			location(startLine(1), startCharacter(1), endLine(1), endCharacter(1)),
			location(startLine(2), startCharacter(1), endLine(2), endCharacter(2)),
			location(startLine(3), startCharacter(1), endLine(3), endCharacter(1)),
			location(startLine(3), startCharacter(2), endLine(3), endCharacter(3))
		);
	}

	private int startLine(int startLine) {
		return startLine;
	}

	private int startCharacter(int startCharacter) {
		return startCharacter;
	}

	private int endLine(int endLine) {
		return endLine;
	}

	private int endCharacter(int endCharacter) {
		return endCharacter;
	}

	private List<Location> allLocationsThatCanBeScanned(Scanner scanner) {
		List<Location> accepted = new ArrayList<>();
		while (scanner.hasNextSymbol()) {
			accepted.add(scanner.nextSymbol().location());
		}
		return accepted;
	}

	private List<Symbol> allSymbolsThatCanBeScanned(Scanner scanner) {
		List<Symbol> accepted = new ArrayList<>();
		while (scanner.hasNextSymbol()) {
			accepted.add(scanner.nextSymbol().symbol());
		}
		return accepted;
	}
}

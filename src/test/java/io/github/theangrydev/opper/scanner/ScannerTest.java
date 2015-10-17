package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.definition.Expression;
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
import static java.util.Collections.singletonList;

public class ScannerTest implements WithAssertions {

	@Test
	public void shouldNotBlowUpWhenAnUnsupportedCharacterIsScanned() {
		Symbol a = new Symbol(1, "a");
		SymbolDefinition aDefinition = definition(a, character('a'));

		Scanner scanner = new BFAScanner(singletonList(aDefinition), new StringReader("zzazzazzz"));
		assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(a, a);
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

	@Test
	public void shouldHandleMultipleSymbolDefinitions() {
		GrammarBuilder grammarBuilder = new GrammarBuilder();
		Symbol identifier = grammarBuilder.symbolByName("Identifier");
		Symbol integer = grammarBuilder.symbolByName("Integer");
		Symbol real = grammarBuilder.symbolByName("Real");
		Symbol whitespace = grammarBuilder.symbolByName("Whitespace");

		List<SymbolDefinition> symbolDefinitions = new ArrayList<>();
		symbolDefinitions.add(definition(identifier, identifier()));
		symbolDefinitions.add(definition(integer, integer()));
		symbolDefinitions.add(definition(real, real()));
		symbolDefinitions.add(definition(whitespace, whitespace()));

		Scanner scanner = new BFAScanner(symbolDefinitions, new StringReader("Ab 20645 9.1"));
		assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(identifier, whitespace, integer, whitespace, real);
	}

	private Expression identifier() {
		return concatenate(uppercase(), repeat(either(lowercase(), uppercase(), digit())));
	}

	private Expression whitespace() {
		return either(character(' '), character('\n'), character('\t'));
	}

	private Expression real() {
		return concatenate(concatenate(integer(), character('.')), integer());
	}

	public Expression integer() {
		return concatenate(digit(), repeat(digit()));
	}

	public Expression uppercase() {
		return either(character('A'), character('B'), character('C'), character('D'), character('E'), character('F'), character('G'), character('H'), character('I'), character('J'), character('K'), character('L'), character('M'), character('N'), character('O'), character('P'), character('Q'), character('R'), character('S'), character('T'), character('U'), character('V'), character('W'), character('X'), character('Y'), character('Z'));
	}

	public Expression lowercase() {
		return either(character('a'), character('b'), character('c'), character('d'), character('e'), character('f'), character('g'), character('h'), character('i'), character('j'), character('k'), character('l'), character('m'), character('n'), character('o'), character('p'), character('q'), character('r'), character('s'), character('t'), character('u'), character('v'), character('w'), character('x'), character('y'), character('z'));
	}

	public Expression digit() {
		return either(character('0'), character('1'), character('2'), character('3'), character('4'), character('5'), character('6'), character('7'), character('8'), character('9'));
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

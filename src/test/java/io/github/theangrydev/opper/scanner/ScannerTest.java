/*
 * Copyright 2015-2020 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.definition.Expression;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.scanner.Location.location;
import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.AnyCharacter.anyCharacter;
import static io.github.theangrydev.opper.scanner.definition.CharacterClassExpression.characterClass;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.NotCharacters.notCharacaters;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;
import static io.github.theangrydev.opper.scanner.definition.SymbolDefinition.definition;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ScannerTest implements WithAssertions {

    @Test
    public void shouldBlowUpWhenAnUnsupportedCharacterIsScanned() throws IOException {
        Symbol a = new Symbol(1, "a");
        SymbolDefinition aDefinition = definition(a, character('a'));

        Scanner scanner = new BFAScannerFactory(singletonList(aDefinition)).scanner(new StringReader("zzazzazzz"));

        assertThatThrownBy(() -> allSymbolsThatCanBeScanned(scanner))
                .hasMessage("TODO: handle character sequences that are not scannable")
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldRecordLocationInformation() throws IOException {
        Symbol content = new Symbol(1, "content");
        SymbolDefinition aDefinition = definition(content, either(character('a'), concatenate(character('b'), character('c'))));

        Symbol newLine = new Symbol(2, "new line");
        SymbolDefinition newLineDefinition = definition(newLine, character('\n'));

        Scanner scanner = new BFAScannerFactory(asList(aDefinition, newLineDefinition)).scanner(new StringReader("a\nbc\nabc"));
        assertThat(allLocationsThatCanBeScanned(scanner)).containsExactly(
                location(startLine(1), startCharacter(1), endLine(1), endCharacter(1)), // line 1 a
                location(startLine(1), startCharacter(2), endLine(2), endCharacter(0)), // line 1 newline
                location(startLine(2), startCharacter(1), endLine(2), endCharacter(2)), // line 2 bc
                location(startLine(2), startCharacter(3), endLine(3), endCharacter(0)), // line 2 newline
                location(startLine(3), startCharacter(1), endLine(3), endCharacter(1)), // line 3 a
                location(startLine(3), startCharacter(2), endLine(3), endCharacter(3))  // line 3 bc
        );
    }

    @Test
    public void shouldHandleMultipleSymbolDefinitions() throws IOException {
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

        Scanner scanner = new BFAScannerFactory(symbolDefinitions).scanner(new StringReader("Ab 20645 9.1"));
        assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(identifier, whitespace, integer, whitespace, real);
    }

    @Test
    public void shouldHandleAnyCharacter() throws IOException {
        Symbol a = new Symbol(1, "a");
        SymbolDefinition aDefinition = definition(a, characterClass(anyCharacter()));

        Scanner scanner = new BFAScannerFactory(singletonList(aDefinition)).scanner(new StringReader("1234"));
        assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(a, a, a, a);
    }

    @Test
    public void shouldHandleNotCharacters() throws IOException {
        Symbol a = new Symbol(1, "a");
        SymbolDefinition aDefinition = definition(a, characterClass(notCharacaters("abcd")));

        Symbol b = new Symbol(2, "b");
        SymbolDefinition bDefinition = definition(b, either(character('a'), character('b'), character('c'), character('d')));

        Scanner scanner = new BFAScannerFactory(asList(aDefinition, bDefinition)).scanner(new StringReader("1abc23d4"));
        assertThat(allSymbolsThatCanBeScanned(scanner)).containsExactly(a, b, b, b, a, a, b, a);
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

    private List<Location> allLocationsThatCanBeScanned(Scanner scanner) throws IOException {
        List<Location> accepted = new ArrayList<>();
        while (scanner.hasNextSymbol()) {
            accepted.add(scanner.nextSymbol().location());
        }
        return accepted;
    }

    private List<Symbol> allSymbolsThatCanBeScanned(Scanner scanner) throws IOException {
        List<Symbol> accepted = new ArrayList<>();
        while (scanner.hasNextSymbol()) {
            accepted.add(scanner.nextSymbol().symbol());
        }
        return accepted;
    }
}

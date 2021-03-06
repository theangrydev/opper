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
package io.github.theangrydev.opper.performance;

import com.google.common.base.Stopwatch;
import io.github.theangrydev.opper.grammar.SymbolFactory;
import io.github.theangrydev.opper.scanner.BFAScannerFactory;
import io.github.theangrydev.opper.scanner.Scanner;
import io.github.theangrydev.opper.scanner.definition.Expression;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;
import static io.github.theangrydev.opper.scanner.definition.SymbolDefinition.definition;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ScannerPerformanceTest implements WithAssertions {

    private static final int NUMBER_OF_CHARACTERS = 100000;

    @Test
    public void shouldScanASimpleExpression() throws IOException {
        SymbolFactory symbolFactory = new SymbolFactory();
        List<SymbolDefinition> symbolDefinitions = new ArrayList<>();
        Expression uppercase = either(character('A'), character('B'), character('C'), character('D'), character('E'), character('F'), character('G'), character('H'), character('I'), character('J'), character('K'), character('L'), character('M'), character('N'), character('O'), character('P'), character('Q'), character('R'), character('S'), character('T'), character('U'), character('V'), character('W'), character('X'), character('Y'), character('Z'));
        Expression digit = either(character('0'), character('1'), character('2'), character('3'), character('4'), character('5'), character('6'), character('7'), character('8'), character('9'));
        Expression lowercase = either(character('a'), character('b'), character('c'), character('d'), character('e'), character('f'), character('g'), character('h'), character('i'), character('j'), character('k'), character('l'), character('m'), character('n'), character('o'), character('p'), character('q'), character('r'), character('s'), character('t'), character('u'), character('v'), character('w'), character('x'), character('y'), character('z'));
        Expression integer = concatenate(digit, repeat(digit));

        symbolDefinitions.add(definition(symbolFactory.createSymbol("&&"), concatenate(character('&'), character('&'))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("||"), concatenate(character('|'), character('|'))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("if"), concatenate(character('i'), character('f'))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("else"), concatenate(concatenate(concatenate(character('e'), character('l')), character('s')), character('e'))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("code"), concatenate(concatenate(concatenate(character('c'), character('o')), character('d')), character('e'))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("api"), concatenate(concatenate(character('a'), character('p')), character('i'))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("*"), character('*')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("+"), character('+')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("-"), character('-')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("/"), character('/')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol(";"), character(';')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol(","), character(',')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("("), character('(')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol(")"), character(')')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("=="), concatenate(character('='), character('='))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("<"), character('<')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol(">"), character('>')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("<="), concatenate(character('<'), character('='))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol(">="), concatenate(character('>'), character('='))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("!="), concatenate(character('!'), character('='))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol(":"), character('!')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("="), character('=')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("."), character('.')));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("Identifier"), concatenate(uppercase, repeat(either(lowercase, uppercase, digit)))));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("Integer"), integer));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("Real"), concatenate(concatenate(integer, character('.')), integer)));
        symbolDefinitions.add(definition(symbolFactory.createSymbol("Whitespace"), either(character(' '), character('\n'), character('\t'))));

        Scanner scanner = new BFAScannerFactory(symbolDefinitions).scanner(characters(NUMBER_OF_CHARACTERS));
        scanAllSymbols(scanner);

        scanner = new BFAScannerFactory(symbolDefinitions).scanner(characters(NUMBER_OF_CHARACTERS));
        Stopwatch stopwatch = Stopwatch.createStarted();

        scanAllSymbols(scanner);

        long elapsed = stopwatch.elapsed(MILLISECONDS);
        System.out.println("Took " + elapsed + "ms");

        assertThat(elapsed).describedAs("Time taken should be less than 100ms").isLessThan(100);
    }

    private CharArrayReader characters(int numberOfCharacters) {
        char[] chars = new char[numberOfCharacters];
        Arrays.fill(chars, '&');
        return new CharArrayReader(chars);
    }

    private void scanAllSymbols(Scanner scanner) throws IOException {
        while (scanner.hasNextSymbol()) {
            scanner.nextSymbol();
        }
    }
}

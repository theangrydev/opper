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
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.parser.EarlyParserFactory;
import io.github.theangrydev.opper.parser.Parser;
import io.github.theangrydev.opper.scanner.Scanner;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static io.github.theangrydev.opper.scanner.FixedScanner.scanner;
import static io.github.theangrydev.opper.scanner.Location.location;
import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;
import static java.util.Collections.nCopies;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Category(PerformanceTests.class)
public class ParserPerformanceTest {

    @Test
    public void shouldRecogniseARightRecursiveGrammarInGoodTime() throws IOException {
        Grammar grammar = new GrammarBuilder()
                .withAcceptanceSymbol("ACCEPT")
                .withStartSymbol("START")
                .withRule("START", "REPEATED", "START")
                .withRule("START", "REPEATED")
                .build();
        Scanner scanner = scanner(nCopies(10000, scannedSymbol(grammar.symbolByName("REPEATED"), "", location(1, 1, 1, 1))));

        Parser parser = new EarlyParserFactory(new DoNothingLogger(), grammar).parser(scanner);

        Stopwatch stopwatch = Stopwatch.createStarted();
        parser.parse();
        long elapsed = stopwatch.elapsed(MILLISECONDS);
        System.out.println("Right Recursive Grammar took " + elapsed + " ms");

        assertThat(elapsed).describedAs("Time taken should be less than 100ms").isLessThan(100);
    }

    @Test
    public void shouldRecogniseALeftRecursiveGrammarInGoodTime() throws IOException {
        Grammar grammar = new GrammarBuilder()
                .withAcceptanceSymbol("ACCEPT")
                .withStartSymbol("START")
                .withRule("START", "START", "REPEATED")
                .withRule("START", "REPEATED")
                .build();
        Scanner scanner = scanner(nCopies(10000, scannedSymbol(grammar.symbolByName("REPEATED"), "", location(1, 1, 1, 1))));

        Parser parser = new EarlyParserFactory(new DoNothingLogger(), grammar).parser(scanner);

        Stopwatch stopwatch = Stopwatch.createStarted();
        parser.parse();
        long elapsed = stopwatch.elapsed(MILLISECONDS);
        System.out.println("Left Recursive Grammar took " + elapsed + " ms");

        assertThat(elapsed).describedAs("Time taken should be less than 100ms").isLessThan(100);
    }

    /**
     * This is NOT expected to scale linearly. It would be better to perhaps reject these from the grammar.
     */
    @Test
    public void shouldRecogniseAGrammarWithAnUnmarkedMiddleRecursionInGoodTime() throws IOException {
        Grammar grammar = new GrammarBuilder()
                .withAcceptanceSymbol("ACCEPT")
                .withStartSymbol("START")
                .withRule("START", "REPEATED", "START", "REPEATED")
                .withRule("START", "REPEATED", "REPEATED")
                .build();
        Scanner scanner = scanner(nCopies(100, scannedSymbol(grammar.symbolByName("REPEATED"), "", location(1, 1, 1, 1))));

        Parser parser = new EarlyParserFactory(new DoNothingLogger(), grammar).parser(scanner);

        Stopwatch stopwatch = Stopwatch.createStarted();
        parser.parse();
        long elapsed = stopwatch.elapsed(MILLISECONDS);
        System.out.println("Unmarked Middle Recursive Grammar took " + elapsed + " ms");

        assertThat(elapsed).describedAs("Time taken should be less than 100ms").isLessThan(100);
    }
}

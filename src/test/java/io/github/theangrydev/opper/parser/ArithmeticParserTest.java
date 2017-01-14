/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
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
package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.FixedScanner;
import io.github.theangrydev.opper.scanner.Scanner;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.IOException;

/**
 * <pre>
 * <P> ::= <S> # the start rule
 * <S> ::= <S> "+" <M> | <M>
 * <M> ::= <M> "*" <T> | <T>
 * <T> ::= "1" | "2" | "3" | "4"
 * </pre>
 *
 * @see <a href="https://en.wikipedia.org/w/index.php?title=Earley_recogniser&oldid=667926718#Example">Early recogniser example</a>
 */
public class ArithmeticParserTest implements WithAssertions {

    @Test
    public void shouldRecogniseALeftRecursiveGrammar() throws IOException {
        Grammar grammar = new GrammarBuilder()
                .withAcceptanceSymbol("P")
                .withStartSymbol("S")
                .withRule("S", "S", "+", "M")
                .withRule("S", "M")
                .withRule("M", "M", "*", "T")
                .withRule("M", "T")
                .withRule("T", "1")
                .withRule("T", "2")
                .withRule("T", "3")
                .withRule("T", "4")
                .build();

        Scanner scanner = FixedScanner.scanner(grammar, "2", "+", "3", "+", "2", "+", "3", "*", "4");

        Parser parser = new EarlyParserFactory(new DoNothingLogger(), grammar).parser(scanner);

        assertThat(parser.parse()).isPresent();
    }
}

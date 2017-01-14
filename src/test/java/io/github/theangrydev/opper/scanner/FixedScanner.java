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
package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.common.Streams;
import io.github.theangrydev.opper.grammar.Grammar;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static io.github.theangrydev.opper.scanner.Location.location;
import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;

public class FixedScanner implements Scanner {

    private final Iterator<ScannedSymbol> symbols;

    private FixedScanner(Iterator<ScannedSymbol> symbols) {
        this.symbols = symbols;
    }

    public static FixedScanner scanner(Grammar grammar, String... symbols) {
        return scanner(grammar, Arrays.asList(symbols));
    }

    public static FixedScanner scanner(Grammar grammar, Iterable<String> symbols) {
        return new FixedScanner(Streams.stream(symbols).map(symbol -> scannedSymbol(grammar.symbolByName(symbol), symbol, location(1, 1, 1, 1))).iterator());
    }

    public static FixedScanner scanner(ScannedSymbol... symbols) {
        return new FixedScanner(Arrays.stream(symbols).iterator());
    }

    public static FixedScanner scanner(List<ScannedSymbol> symbols) {
        return new FixedScanner(symbols.iterator());
    }

    @Override
    public ScannedSymbol nextSymbol() {
        return symbols.next();
    }

    @Override
    public boolean hasNextSymbol() {
        return symbols.hasNext();
    }
}

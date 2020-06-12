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

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.bfa.BFA;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;

import java.io.IOException;
import java.io.Reader;

import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;

public class BFAScanner implements Scanner {

    /**
     * The {@link Reader} will return -1 when the end of the input is reached. This is the result of (char) -1.
     * Although \uFFFF is a valid character, we are assuming it never appears in scanned text.
     * If it did, the scanning would end as if the end of the stream was reached.
     */
    private static final char READER_END_OF_INPUT_MARKER = '\uFFFF';

    private final Reader charactersToParse;
    private final BFA bfa;

    /**
     * This keeps track of the start and end of the current {@link Symbol} being processed, in order to produce a {@link Location}.
     */
    private PositionTracker positionTracker = new PositionTracker();

    /**
     * The character that is currently being inspected.
     * <p>
     * This starts off as the first character from the reader, so that the first call to {@link #hasNextSymbol} is accurate.
     * <p>
     * A non zero frontier means there are more characters to parse in the current {@link Symbol}, so scan the next character.
     * A zero frontier means there are no more characters to parse in the current {@link Symbol}, so we do not scan another
     * character and leave the character ready for the next call to {@link #nextSymbol()}.
     */
    private char character;

    /**
     * The characters that make up the {@link Symbol} that is currently being parsed.
     */
    private StringBuilder symbolCharacters;

    private BFAScanner(BFA bfa, Reader charactersToParse, char firstCharacter) {
        this.bfa = bfa;
        this.charactersToParse = charactersToParse;
        this.positionTracker = new PositionTracker();
        this.character = firstCharacter;
        this.symbolCharacters = new StringBuilder();
    }

    public static BFAScanner createBFAScanner(BFA bfa, Reader charactersToParse) throws IOException {
        char firstCharacter = (char) charactersToParse.read();
        return new BFAScanner(bfa, charactersToParse, firstCharacter);
    }

    @Override
    public ScannedSymbol nextSymbol() throws IOException {
        positionTracker.markSymbolStart();
        symbolCharacters.setLength(0);

        BinaryDecisionDiagram nextFrontier = bfa.initialState();
        BinaryDecisionDiagram frontier = nextFrontier.copy();
        nextFrontier = bfa.transition(nextFrontier, character);

        while (!nextFrontier.isZero()) {
            positionTracker.consider(character);
            symbolCharacters.append(character);

            frontier.discard();
            frontier = nextFrontier.copy();

            character = (char) charactersToParse.read();
            nextFrontier = bfa.transition(nextFrontier, character);
        }

        if (symbolCharacters.length() == 0) {
            throw new UnsupportedOperationException("TODO: handle character sequences that are not scannable");
        }

        Symbol acceptingSymbol = bfa.acceptingSymbol(frontier);
        frontier.discard();
        return scannedSymbol(acceptingSymbol, symbolCharacters.toString(), positionTracker.currentLocation());
    }

    @Override
    public boolean hasNextSymbol() {
        return character != READER_END_OF_INPUT_MARKER;
    }

    private static class PositionTracker {
        private int currentLineNumber;
        private int currentCharacterNumber;
        private int markedLineNumber;
        private int markedCharacterNumber;

        public PositionTracker() {
            currentLineNumber = 1;
            currentCharacterNumber = 0;
            markedCharacterNumber = 1;
        }

        public void markSymbolStart() {
            markedLineNumber = currentLineNumber;
            markedCharacterNumber = currentCharacterNumber + 1;
        }

        public void consider(char character) {
            currentCharacterNumber++;
            if (character == '\n') {
                currentLineNumber++;
                currentCharacterNumber = 0;
            }
        }

        public Location currentLocation() {
            return Location.location(markedLineNumber, markedCharacterNumber, currentLineNumber, currentCharacterNumber);
        }
    }
}

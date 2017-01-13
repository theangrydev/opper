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

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.bfa.BFA;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;

import java.io.IOException;
import java.io.Reader;

import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;

public class BFAScanner implements Scanner {

	private static final char READER_END_OF_INPUT_MARKER = '\uFFFF'; // this is the result of (char) -1

	private final Reader charactersToParse;
	private final BFA bfa;

	private PositionTracker positionTracker = new PositionTracker();
	private char character;
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
		BinaryDecisionDiagram frontier;

		do {
			frontier = nextFrontier.copy();
			nextFrontier = bfa.transition(nextFrontier, character);

            if (!nextFrontier.isZero()) {
                positionTracker.consider(character);
                symbolCharacters.append(character);
                frontier.discard();
				character = (char) charactersToParse.read();
            }
        } while (!nextFrontier.isZero());

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

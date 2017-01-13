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
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Optional;

import static io.github.theangrydev.opper.scanner.ScannedSymbol.scannedSymbol;

public class BFAScanner implements Scanner {

	private final PushbackReader charactersToParse;
	private final BFA bfa;
	private BinaryDecisionDiagram frontier;
	private StringBuilder nextCharacters;
	private Position position;
	private int read;

	public BFAScanner(BFA bfa, Reader charactersToParse) {
		this.bfa = bfa;
		this.charactersToParse = new PushbackReader(charactersToParse, 1);
		this.position = new Position();
	}

	@Override
	public ScannedSymbol nextSymbol() {
		prepareForNextSymbol();
		BinaryDecisionDiagram lastNonZeroFromFrontier = scanUntilFrontierIsZero();
		Optional<Symbol> accepted = bfa.checkAcceptance(lastNonZeroFromFrontier);
		if (!accepted.isPresent()) {
			throw new UnsupportedOperationException("TODO: handle last non zero from frontier that is not an accepting state");
		}
		lastNonZeroFromFrontier.discard();
		pushback((char) read);
		return acceptedSymbol(accepted.get());
	}

	@Override
	public boolean hasNextSymbol() {
		return read != -1;
	}

	private BinaryDecisionDiagram scanUntilFrontierIsZero() {
		BinaryDecisionDiagram lastNonZeroFromFrontier = null;
	    while (!frontier.isZero()) {
			read = read();
			char character = (char) read;
			frontier = bfa.transition(frontier, character);
			position.consider(character);
			if (!frontier.isZero()) {
				if (lastNonZeroFromFrontier != null) {
					lastNonZeroFromFrontier.discard();
				}
				lastNonZeroFromFrontier = frontier.copy();
				nextCharacters.append(character);
			}
		}
		if (lastNonZeroFromFrontier == null) {
	    	throw new UnsupportedOperationException("TODO: handle character sequences that are not scannable");
		}
		return lastNonZeroFromFrontier;
	}

	private void pushback(char character) {
		position.unconsider(character);
		try {
			charactersToParse.unread(character);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private ScannedSymbol acceptedSymbol(Symbol acceptedSymbol) {
		return scannedSymbol(acceptedSymbol, nextCharacters.toString(), position.location());
	}

	private static class Position {
		private int currentLineNumber;
		private int currentCharacterNumber;
		private int markedLineNumber;
		private int markedCharacterNumber;
		private int previousLineCharacterNumber;

		public Position() {
			currentLineNumber = 1;
			currentCharacterNumber = 0;
			markedCharacterNumber = 1;
		}

		public void mark() {
			markedLineNumber = currentLineNumber;
			markedCharacterNumber = currentCharacterNumber + 1;
		}

		public void consider(char character) {
			previousLineCharacterNumber = currentCharacterNumber;
			currentCharacterNumber++;
			if (character == '\n') {
				currentLineNumber++;
				currentCharacterNumber = 0;
			}
		}

		public void unconsider(char character) {
			currentCharacterNumber--;
			if (character == '\n') {
				currentLineNumber--;
				currentCharacterNumber = previousLineCharacterNumber;
			}
		}

		public Location location() {
			return Location.location(markedLineNumber, markedCharacterNumber, currentLineNumber, currentCharacterNumber);
		}
	}

	private void prepareForNextSymbol() {
		nextCharacters = new StringBuilder();
		frontier = bfa.initialState();
		position.mark();
	}

	private int read() {
		try {
			return charactersToParse.read();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}

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
	private ScannedSymbol next;
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
		return next;
	}

	@Override
	public boolean hasNextSymbol() {
		while (read != -1) {
			prepareForNextSymbol();
			BinaryDecisionDiagram lastNonZeroFromFrontier = scanUntilFrontierIsZero();
			if (lastNonZeroFromFrontier == null) {
				continue;
			}
			Optional<Symbol> accepted = bfa.checkAcceptance(lastNonZeroFromFrontier);
			lastNonZeroFromFrontier.discard();
			if (accepted.isPresent()) {
				pushback((char) read);
				next = acceptedSymbol(accepted.get());
				return true;
			}
		}
		return false;
	}

	private BinaryDecisionDiagram scanUntilFrontierIsZero() {
		BinaryDecisionDiagram lastNonZeroFromFrontier = null;
	    while (!frontier.isZero() && read != -1) {
			read = read();
			if (read == -1) {
				break;
			}
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

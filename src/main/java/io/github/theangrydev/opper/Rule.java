package io.github.theangrydev.opper;

public class Rule {

	private final Symbol left;
	private final SymbolSequence right;

	public Rule(Symbol left, SymbolSequence right) {
		this.left = left;
		this.right = right;
	}

	public int length() {
		return right.length();
	}

	public Symbol symbolAt(int dotPosition) {
		return right.symbolAt(dotPosition);
	}

	public Symbol left() {
		return left;
	}

	@Override
	public java.lang.String toString() {
		return left + " -> " + right;
	}
}

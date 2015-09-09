package io.github.theangrydev.opper.grammar;

public class Rule {

	private final int id;
	private final Symbol left;
	private final SymbolSequence right;

	public Rule(int id, Symbol left, SymbolSequence right) {
		this.id = id;
		this.left = left;
		this.right = right;
	}

	public int length() {
		return right.length();
	}

	public Symbol symbolAt(int dotPosition) {
		return right.symbolAt(dotPosition);
	}

	public Symbol rightPrefix() {
		return symbolAt(0);
	}

	public Symbol left() {
		return left;
	}

	@Override
	public java.lang.String toString() {
		return left + " -> " + right;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final Rule other = (Rule) obj;
		return this.id == other.id;
	}
}

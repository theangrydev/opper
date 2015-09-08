package io.github.theangrydev.opper;

import java.util.Objects;

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
		return Objects.hash(left, right);
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
		return Objects.equals(this.left, other.left)
			&& Objects.equals(this.right, other.right);
	}
}

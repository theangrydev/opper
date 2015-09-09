package io.github.theangrydev.opper.grammar;

public class Rule {

	private final int id;
	private final Symbol start;
	private final SymbolSequence derivation;

	public Rule(int id, Symbol start, SymbolSequence derivation) {
		this.id = id;
		this.start = start;
		this.derivation = derivation;
	}

	public int derivationLength() {
		return derivation.length();
	}

	public Symbol derivation(int dotPosition) {
		return derivation.symbolAt(dotPosition);
	}

	public Symbol derivationPrefix() {
		return derivation(0);
	}

	public Symbol start() {
		return start;
	}

	@Override
	public java.lang.String toString() {
		return start + " -> " + derivation;
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

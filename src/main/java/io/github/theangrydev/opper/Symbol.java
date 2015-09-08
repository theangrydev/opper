package io.github.theangrydev.opper;

import java.util.Objects;

public class Symbol {

	private final int index;
	private final String name;

	public Symbol(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int index() {
		return index;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final Symbol other = (Symbol) obj;
		return Objects.equals(this.index, other.index)
			&& Objects.equals(this.name, other.name);
	}
}

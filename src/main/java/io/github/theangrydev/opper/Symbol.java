package io.github.theangrydev.opper;

import java.lang.*;
import java.lang.String;

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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Symbol symbol = (Symbol) o;
		return index == symbol.index;

	}

	@Override
	public int hashCode() {
		return index;
	}
}

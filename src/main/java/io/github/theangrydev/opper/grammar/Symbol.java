package io.github.theangrydev.opper.grammar;

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
}

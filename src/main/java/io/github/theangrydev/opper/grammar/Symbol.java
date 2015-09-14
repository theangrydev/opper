package io.github.theangrydev.opper.grammar;

public class Symbol {

	private final int id;
	private final String name;

	public Symbol(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int id() {
		return id;
	}

	@Override
	public String toString() {
		return name;
	}
}

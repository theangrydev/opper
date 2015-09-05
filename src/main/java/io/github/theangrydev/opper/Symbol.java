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
}

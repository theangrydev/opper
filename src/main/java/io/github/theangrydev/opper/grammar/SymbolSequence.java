/*
 * Copyright 2015 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.grammar;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class SymbolSequence {

	private final List<Symbol> symbols;

	public SymbolSequence(Symbol... symbols) {
		this.symbols = Arrays.asList(symbols);
	}

	public int length() {
		return symbols.size();
	}

	public Symbol symbolAt(int dotPosition) {
		return symbols.get(dotPosition);
	}

	public List<Symbol> symbols() {
		return symbols;
	}

	@Override
	public String toString() {
		return symbols.stream().map(Symbol::toString).collect(joining(" "));
	}
}

/**
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
package io.github.theangrydev.opper.parser.early;

import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class TransitionsEarlySetsBySymbol {

	private final List<Symbol> symbols;
	private final List<TransitionsEarlySet> earlySets;

	public TransitionsEarlySetsBySymbol(List<Symbol> symbols) {
		this.symbols = symbols;
		this.earlySets = new ObjectArrayList<>(symbols.size());
		for (int i = 0; i < symbols.size(); i++) {
			earlySets.add(new TransitionsEarlySet());
		}
	}

	public TransitionsEarlySet itemsThatCanAdvanceGiven(Symbol symbol) {
		return earlySets.get(symbol.id());
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append('\n');
		for (Symbol symbol : symbols) {
			string.append(symbol);
			string.append(':');
			string.append(earlySets.get(symbol.id()));
			string.append('\n');
		}
		return string.toString();
	}
}

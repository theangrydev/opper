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

import java.util.List;
import java.util.function.Predicate;

public class Rule {

	private final int id;
	private final Symbol trigger;
	private final SymbolSequence derivation;

	public Rule(int id, Symbol trigger, SymbolSequence derivation) {
		this.id = id;
		this.trigger = trigger;
		this.derivation = derivation;
	}

	public static Predicate<Rule> triggeredBy(Symbol symbol) {
		return rule -> symbol == rule.trigger();
	}

	public int derivationLength() {
		return derivation.length();
	}

	public int derivationSuffixDotPosition() {
		return derivation.length() - 1;
	}

	public Symbol derivationSuffix() {
		return derivation(derivationSuffixDotPosition());
	}

	public boolean isRightRecursive() {
		return derivationSuffix() == trigger();
	}

	public Symbol derivation(int dotPosition) {
		return derivation.symbolAt(dotPosition);
	}

	public List<Symbol> derivation() {
		return derivation.symbols();
	}

	public Symbol derivationPrefix() {
		return derivation(0);
	}

	public Symbol trigger() {
		return trigger;
	}

	public int id() {
		return id;
	}

	@Override
	public String toString() {
		return trigger + " -> " + derivation;
	}
}

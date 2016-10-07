/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
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

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EarlySet implements Iterable<EarlyItem> {

	private final List<EarlyItem> earlyItems;
	private final Grammar grammar;

	public EarlySet(Grammar grammar) {
		this.grammar = grammar;
		this.earlyItems = new ObjectArrayList<>();
	}

	public void add(EarlyItem earlyItem) {
		earlyItems.add(earlyItem);
	}

	public boolean isEmpty() {
		return earlyItems.isEmpty();
	}

	public void reset() {
		earlyItems.clear();
	}

	public Optional<EarlyItem> completedAcceptanceRule(TransitionsEarlySetsBySymbol initialTransitions) {
		return earlyItems.stream().filter(earlyItem -> earlyItem.hasCompletedAcceptanceRule(initialTransitions, grammar.acceptanceSymbol())).findAny();
	}

	@Override
	public Iterator<EarlyItem> iterator() {
		return earlyItems.iterator();
	}

	public boolean isLeoUnique(DottedRule dottedRule) {
		Optional<Symbol> penult = dottedRule.penult();
		return penult.isPresent() && containsAndPenultUnique(dottedRule, penult.get());
	}

	public int size() {
		return earlyItems.size();
	}

	private boolean containsAndPenultUnique(DottedRule dottedRule, Symbol penult) {
		boolean contains = false;
		for (int i = earlyItems.size() - 1; i >= 0; i--) {
			DottedRule testRule = earlyItems.get(i).dottedRule();
			if (testRule == dottedRule) {
				contains = true;
				continue;
			}
			Optional<Symbol> testPenult = testRule.penult();
			if (!testPenult.isPresent()) {
				continue;
			}
			if (testPenult.get() == penult) {
				return false;
			}
		}
		return contains;
	}

	public boolean contains(EarlyItem earlyItem) {
		for (int i = earlyItems.size() - 1; i >= 0; i--) {
			if (earlyItems.get(i).sameAs(earlyItem)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return earlyItems.toString();
	}
}

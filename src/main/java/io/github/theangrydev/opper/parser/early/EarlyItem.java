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
package io.github.theangrydev.opper.parser.early;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.tree.ParseTree;
import io.github.theangrydev.opper.parser.tree.ParseTreeNode;
import io.github.theangrydev.opper.scanner.Location;

import static io.github.theangrydev.opper.parser.tree.ParseTreeNode.node;

public abstract class EarlyItem {
	protected final ParseTreeNode parseTree;
	protected final TransitionsEarlySetsBySymbol origin;
	protected final DottedRule dottedRule;

	protected EarlyItem(ParseTreeNode parseTree, TransitionsEarlySetsBySymbol origin, DottedRule dottedRule) {
		this.origin = origin;
		this.dottedRule = dottedRule;
		this.parseTree = parseTree;
	}

	protected EarlyItem(TransitionsEarlySetsBySymbol origin, DottedRule dottedRule) {
		this(node(dottedRule.rule()), origin, dottedRule);
	}

	public DottedRule dottedRule() {
		return dottedRule;
	}

	public TransitionsEarlySetsBySymbol origin() {
		return origin;
	}

	public TransitionsEarlySet itemsThatCanAdvanceWhenThisIsComplete() {
		return origin.itemsThatCanAdvanceGiven(dottedRule.trigger());
	}

	public boolean hasCompletedAcceptanceRule(TransitionsEarlySetsBySymbol initialTransitions, Symbol acceptanceSymbol) {
		return origin == initialTransitions && dottedRule.isCompletedAcceptanceRule(acceptanceSymbol);
	}

	public boolean isComplete() {
		return dottedRule.isComplete();
	}

	public Symbol postDot() {
		return dottedRule.postDot();
	}

	public ParseTreeNode parseTree() {
		return parseTree;
	}

	protected abstract EarlyItem advance();

	public EarlyItem advance(String content, Location location) {
		EarlyItem advance = advance();
		advance.parseTree.withContent(content, location);
		return advance;
	}

	public EarlyItem advance(EarlyItem earlyItem) {
		return advance(earlyItem.parseTree);
	}

	public EarlyItem advance(ParseTree parseTree) {
		EarlyItem advance = advance();
		advance.parseTree.withChild(parseTree);
		return advance;
	}

	public boolean sameAs(EarlyItem other) {
		return this == other || this.origin == other.origin && this.dottedRule == other.dottedRule;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + dottedRule.toString() + " | " + parseTree;
	}
}

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
package io.github.theangrydev.opper.parser.tree;

import io.github.theangrydev.opper.grammar.Rule;
import io.github.theangrydev.opper.scanner.Location;

import java.util.ArrayList;
import java.util.List;

import static io.github.theangrydev.opper.parser.tree.ParseTreeLeaf.leaf;

public class ParseTreeNode extends ParseTree {
	private List<ParseTree> children;

	private ParseTreeNode(Rule rule, List<ParseTree> children) {
		super(rule);
		this.children = children;
	}

	public static ParseTreeNode node(Rule rule) {
		return new ParseTreeNode(rule, new ArrayList<>(rule.derivationLength()));
	}

	public ParseTreeNode copy() {
		return new ParseTreeNode(rule(), new ArrayList<>(children));
	}

	public void withContent(String content, Location location) {
		children.add(leaf(rule(), content, location));
	}

	public void withChild(ParseTree child) {
		children.add(child);
	}

	@Override
	public String content() {
		return "";
	}

	@Override
	public List<ParseTree> children() {
		return children;
	}

	@Override
	public Location location() {
		if (children.isEmpty()) {
			return Location.location(0, 0, 0, 0);
		}
		return Location.between(firstChild().location(), lastChild().location());
	}

	@Override
	public String toString() {
		return rule().toString() + children.toString();
	}

	public ParseTree firstChild() {
		return children().get(0);
	}

	public ParseTree lastChild() {
		return children().get(children().size() - 1);
	}
}

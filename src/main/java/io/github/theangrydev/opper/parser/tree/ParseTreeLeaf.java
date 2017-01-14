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

import java.util.Collections;
import java.util.List;

public class ParseTreeLeaf extends ParseTree {
    private final Location location;
    private final String content;

    private ParseTreeLeaf(Rule rule, String content, Location location) {
        super(rule);
        this.content = content;
        this.location = location;
    }

    public static ParseTreeLeaf leaf(Rule rule, String content, Location location) {
        return new ParseTreeLeaf(rule, content, location);
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public List<ParseTree> children() {
        return Collections.emptyList();
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public String toString() {
        return rule().toString() + content;
    }
}

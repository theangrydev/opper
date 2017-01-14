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

import io.github.theangrydev.opper.parser.tree.ParseTreeNode;

public class TraditionalEarlyItem extends EarlyItem {

    public TraditionalEarlyItem(TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
        super(transitions, dottedRule);
    }

    private TraditionalEarlyItem(ParseTreeNode parseTree, TransitionsEarlySetsBySymbol transitions, DottedRule dottedRule) {
        super(parseTree, transitions, dottedRule);
    }

    @Override
    public EarlyItem advance() {
        return new TraditionalEarlyItem(parseTree.copy(), origin, dottedRule.advance());
    }
}

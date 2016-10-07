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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TransitionsEarlySet implements Iterable<EarlyItem> {

	private static final List<EarlyItem> NO_TRANSITIONS = Collections.emptyList();
	private static final Optional<LeoItem> NO_LEO_ITEM = Optional.empty();

	private Optional<LeoItem> leoItem = NO_LEO_ITEM;
	private List<EarlyItem> earlyItems = NO_TRANSITIONS;

	public void addLeoItem(LeoItem leoItem) {
		earlyItems = Collections.singletonList(leoItem);
		this.leoItem = Optional.of(leoItem);
	}

	public void addEarlyItem(EarlyItem earlyItem) {
		if (earlyItems == NO_TRANSITIONS) {
			earlyItems = new ObjectArrayList<>();
		}
		earlyItems.add(earlyItem);
	}

	@Override
	public Iterator<EarlyItem> iterator() {
		return earlyItems.iterator();
	}

	public Optional<LeoItem> leoItem() {
		return leoItem;
	}

	@Override
	public String toString() {
		return earlyItems.toString();
	}

	public boolean hasLeoItem() {
		return leoItem().isPresent();
	}
}

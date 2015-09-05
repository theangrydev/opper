package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import java.lang.*;
import java.lang.String;
import java.util.Iterator;
import java.util.SortedSet;

public class EarlySet implements Iterable<EarlyItem> {

	private final SortedSet<EarlyItem> earlyItems;
	private final Object2IntMap<DottedRule> oldRules;

	public EarlySet() {
		this.earlyItems = new ObjectLinkedOpenHashSet<>();
		this.oldRules = new Object2IntArrayMap<>();
	}

	public void add(int earlySetIndex, EarlyItem earlyItem) {
		oldRules.put(earlyItem.dottedRule(), earlySetIndex);
		earlyItems.add(earlyItem);
	}

	public boolean isEmpty() {
		return earlyItems.isEmpty();
	}

	@Override
	public Iterator<EarlyItem> iterator() {
		return earlyItems.iterator();
	}

	public boolean isNew(int earlySetIndex, EarlyItem earlyItem) {
		return earlySetIndex != oldRules.getInt(earlyItem.dottedRule());
	}

	@Override
	public String toString() {
		return earlyItems.toString();
	}
}

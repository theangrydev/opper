package io.github.theangrydev.opper.recogniser;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TransitionsEarlySet implements Iterable<EarlyOrLeoItem> {

	private static final List<EarlyOrLeoItem> NO_TRANSITIONS = Collections.emptyList();
	private static final Optional<LeoItem> NO_LEO_ITEM = Optional.empty();

	private Optional<LeoItem> leoItem = NO_LEO_ITEM;
	private List<EarlyOrLeoItem> earlyItems = NO_TRANSITIONS;

	public void add(LeoItem leoItem) {
		earlyItems = Collections.singletonList(leoItem);
		this.leoItem = Optional.of(leoItem);
	}

	public void add(EarlyItem earlyItem) {
		if (earlyItems == NO_TRANSITIONS) {
			earlyItems = new ObjectArrayList<>();
		}
		earlyItems.add(earlyItem);
	}

	@Override
	public Iterator<EarlyOrLeoItem> iterator() {
		return earlyItems.iterator();
	}

	public Optional<LeoItem> leoItem() {
		return leoItem;
	}

	@Override
	public String toString() {
		return earlyItems.toString();
	}
}

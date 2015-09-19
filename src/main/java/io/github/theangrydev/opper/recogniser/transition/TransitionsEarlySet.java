package io.github.theangrydev.opper.recogniser.transition;

import io.github.theangrydev.opper.recogniser.item.EarlyItem;
import io.github.theangrydev.opper.recogniser.item.LeoItem;
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
}

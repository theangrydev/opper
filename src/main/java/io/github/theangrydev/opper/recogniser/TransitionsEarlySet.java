package io.github.theangrydev.opper.recogniser;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TransitionsEarlySet implements Iterable<EarlyOrLeoItem> {

	private static final List<EarlyOrLeoItem> NONE = Collections.emptyList();

	private boolean isLeoSet;
	private List<EarlyOrLeoItem> earlyItems = NONE;

	public void add(LeoItem leoItem) {
		earlyItems = Collections.singletonList(leoItem);
		isLeoSet = true;
	}

	public void add(EarlyItem earlyItem) {
		if (earlyItems == NONE) {
			earlyItems = new ObjectArrayList<>();
		}
		earlyItems.add(earlyItem);
	}

	@Override
	public Iterator<EarlyOrLeoItem> iterator() {
		return earlyItems.iterator();
	}

	public Optional<EarlyOrLeoItem> leoItem() {
		if (isLeoSet) {
			return Optional.of(earlyItems.get(0));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public String toString() {
		return earlyItems.toString();
	}
}

package io.github.theangrydev.opper.recogniser;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TransitionsEarlySet implements Iterable<EarlyOrLeoItem> {

	private boolean isLeoSet;
	private List<EarlyOrLeoItem> earlyItems = Collections.emptyList();

	public void add(LeoItem leoItem) {
		earlyItems = Collections.singletonList(leoItem);
		isLeoSet = true;
	}

	public void add(EarlyItem earlyItem) {
		if (earlyItems.isEmpty()) {
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
}

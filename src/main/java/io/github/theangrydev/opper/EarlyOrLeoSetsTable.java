package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.lang.*;
import java.lang.String;
import java.util.List;
import java.util.Set;

public class EarlyOrLeoSetsTable {

	private final List<Set<EarlyOrLeoItem>> earlySets;

	public EarlyOrLeoSetsTable(int size) {
		this.earlySets = new ObjectArrayList<>(size);
		for (int i = 0; i < size; i++) {
			earlySets.add(new ObjectArraySet<>());
		}
	}

	public Set<EarlyOrLeoItem> earlySet(int location) {
		return earlySets.get(location);
	}

	@Override
	public String toString() {
		return earlySets.toString();
	}
}

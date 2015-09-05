package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.lang.*;
import java.lang.String;
import java.util.List;
import java.util.Set;

public class EarlyOrLeoSetsTable {

	private final List<Set<EarlyOrLeoItem>> earlySets;

	public EarlyOrLeoSetsTable() {
		this.earlySets = new ObjectArrayList<>();
	}

	public Set<EarlyOrLeoItem> earlySet(int location) {
		return earlySets.get(location);
	}

	public void expand() {
		earlySets.add(new ObjectArraySet<>());
	}

	@Override
	public String toString() {
		return earlySets.toString();
	}
}

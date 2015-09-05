package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.*;
import java.lang.String;
import java.util.List;

public class EarlySetsTable {

	private final List<EarlySet> earlySets;

	public EarlySetsTable(int corupusSize) {
		int size = corupusSize + 1;
		this.earlySets = new ObjectArrayList<>(size);
		for (int i = 0; i < size; i++) {
			earlySets.add(new EarlySet());
		}
	}

	public EarlySet earlySet(int location) {
		return earlySets.get(location);
	}

	@Override
	public String toString() {
		return earlySets.toString();
	}
}

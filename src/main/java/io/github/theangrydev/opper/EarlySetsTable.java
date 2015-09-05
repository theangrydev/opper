package io.github.theangrydev.opper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.*;
import java.lang.String;
import java.util.List;

public class EarlySetsTable {

	private final List<EarlySet> earlySets;

	public EarlySetsTable() {
		this.earlySets = new ObjectArrayList<>();
	}

	public void expand() {
		earlySets.add(new EarlySet());
	}

	public EarlySet earlySet(int location) {
		return earlySets.get(location);
	}

	public EarlySet lastEntry() {
		return earlySet(earlySets.size() - 1);
	}

	@Override
	public String toString() {
		return earlySets.toString();
	}
}

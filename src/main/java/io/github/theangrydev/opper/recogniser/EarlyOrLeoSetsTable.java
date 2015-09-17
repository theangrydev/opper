package io.github.theangrydev.opper.recogniser;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class EarlyOrLeoSetsTable {

	private final List<TransitionsEarlySet> earlySets;

	public EarlyOrLeoSetsTable() {
		this.earlySets = new ObjectArrayList<>();
	}

	public TransitionsEarlySet earlySet(int location) {
		return earlySets.get(location);
	}

	public void expand() {
		earlySets.add(new TransitionsEarlySet());
	}

	@Override
	public String toString() {
		return earlySets.stream().map(Object::toString).collect(joining("\n", "\n", "\n"));
	}
}

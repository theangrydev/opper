package io.github.theangrydev.opper.recogniser;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;

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
		return earlySets.stream().map(Object::toString).collect(joining("\n", "\n", "\n"));
	}
}

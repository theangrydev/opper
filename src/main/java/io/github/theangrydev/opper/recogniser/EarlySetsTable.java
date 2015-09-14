package io.github.theangrydev.opper.recogniser;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

import static java.util.stream.Collectors.joining;

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
		return earlySets.stream().map(Object::toString).collect(joining("\n", "\n", "\n"));
	}
}

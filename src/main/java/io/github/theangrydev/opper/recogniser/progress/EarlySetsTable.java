package io.github.theangrydev.opper.recogniser.progress;

import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.recogniser.item.EarlyItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.function.Predicate;

import static io.github.theangrydev.opper.common.Streams.stream;
import static java.util.stream.Collectors.joining;

public class EarlySetsTable {

	private final List<EarlySet> earlySets;
	private final Grammar grammar;

	public EarlySetsTable(Grammar grammar) {
		this.grammar = grammar;
		this.earlySets = new ObjectArrayList<>();
	}

	public void expand() {
		earlySets.add(new EarlySet());
	}

	public EarlySet earlySet(int location) {
		return earlySets.get(location);
	}

	public boolean lastEarlySetHasCompletedAcceptanceRule() {
		return stream(lastEntry()).anyMatch(hasCompletedAcceptanceRule());
	}

	private Predicate<EarlyItem> hasCompletedAcceptanceRule() {
		return earlyItem -> earlyItem.hasCompletedAcceptanceRule(grammar.acceptanceSymbol());
	}

	private EarlySet lastEntry() {
		return earlySet(earlySets.size() - 1);
	}

	@Override
	public String toString() {
		return earlySets.stream().map(Object::toString).collect(joining("\n", "\n", "\n"));
	}
}

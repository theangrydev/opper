package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static io.github.theangrydev.opper.common.Predicates.not;

public class EarlySet implements Iterable<EarlyItem> {

	private final List<EarlyItem> earlyItems;

	public EarlySet() {
		this.earlyItems = new ObjectArrayList<>();
	}

	public void addIfNew(EarlyItem earlyItem) {
		if (!earlyItems.contains(earlyItem)) {
			earlyItems.add(earlyItem);
		}
	}

	public boolean isEmpty() {
		return earlyItems.isEmpty();
	}

	@Override
	public Iterator<EarlyItem> iterator() {
		return earlyItems.iterator();
	}

	public boolean isLeoUnique(DottedRule dottedRule) {
		Optional<Symbol> penult = dottedRule.penult();
		return penult.isPresent() && contains(dottedRule) && penultUnique(dottedRule, penult);
	}

	private boolean contains(DottedRule dottedRule) {
		return earlyItems.stream()
			.map(EarlyItem::dottedRule)
			.anyMatch(dottedRule::equals);
	}

	private boolean penultUnique(DottedRule dottedRule, Optional<Symbol> penult) {
		return earlyItems.stream()
			.map(EarlyItem::dottedRule)
			.filter(not(dottedRule::equals))
			.map(DottedRule::penult)
			.noneMatch(penult::equals);
	}

	@Override
	public String toString() {
		return earlyItems.toString();
	}
}

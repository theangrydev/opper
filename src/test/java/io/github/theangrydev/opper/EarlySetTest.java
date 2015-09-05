package io.github.theangrydev.opper;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EarlySetTest implements WithAssertions {

	private final EarlyItemFactory earlyItemFactory = new EarlyItemFactory();
	private final RuleFactory ruleFactory = new RuleFactory();
	private final SymbolFactory symbolFactory = new SymbolFactory();

	@Test
	public void shouldBeAbleToIterateItemsThatWereAddedDuringTheSameIteration() {
		EarlyItem oldItem = createEarlyItem("old");
		EarlyItem newItem = createEarlyItem("new");

		EarlySet earlySet = new EarlySet();
		earlySet.add(0, oldItem);

		List<EarlyItem> itemsSeen = new ArrayList<>();
		boolean addedNewItem = false;
		for (EarlyItem earlyItem : earlySet) {
			if (!addedNewItem) {
				earlySet.add(0, newItem);
				addedNewItem = true;
			}
			itemsSeen.add(earlyItem);
		}

		assertThat(itemsSeen).containsExactly(oldItem, newItem);
	}

	private EarlyItem createEarlyItem(String symbolName) {
		return earlyItemFactory.createEarlyItem(ruleFactory.createRule(symbolFactory.createSymbol(symbolName)), 0);
	}
}

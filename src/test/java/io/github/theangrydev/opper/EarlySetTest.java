package io.github.theangrydev.opper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EarlySetTest {

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
		boolean done = false;
		for (EarlyItem earlyItem : earlySet) {
			if (!done) {
				earlySet.add(0, newItem);
				done = true;
			}
			itemsSeen.add(earlyItem);
		}

		org.assertj.core.api.Assertions.assertThat(itemsSeen).hasSize(2);
	}

	private EarlyItem createEarlyItem(String symbolName) {
		return earlyItemFactory.createEarlyItem(ruleFactory.createRule(symbolFactory.createSymbol(symbolName)), 0);
	}
}

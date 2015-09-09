package io.github.theangrydev.opper.recogniser;

import io.github.theangrydev.opper.grammar.RuleFactory;
import io.github.theangrydev.opper.grammar.SymbolFactory;
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
		earlySet.add(oldItem);

		List<EarlyItem> itemsSeen = new ArrayList<>();
		boolean addedNewItem = false;
		for (EarlyItem earlyItem : earlySet) {
			if (!addedNewItem) {
				earlySet.add(newItem);
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

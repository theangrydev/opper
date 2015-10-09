package io.github.theangrydev.opper.parser.progress;

import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.parser.item.EarlyItem;
import io.github.theangrydev.opper.parser.item.TraditionalEarlyItem;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class EarlySetTest implements WithAssertions {

	@Test
	public void shouldBeAbleToIterateItemsThatWereAddedDuringTheSameIteration() {
		TraditionalEarlyItem oldItem = createEarlyItem();
		TraditionalEarlyItem newItem = createEarlyItem();

		EarlySet earlySet = new EarlySet(new GrammarBuilder().build());
		earlySet.addIfNew(oldItem);

		List<EarlyItem> itemsSeen = new ArrayList<>();
		boolean addedNewItem = false;
		for (EarlyItem earlyItem : earlySet) {
			if (!addedNewItem) {
				earlySet.addIfNew(newItem);
				addedNewItem = true;
			}
			itemsSeen.add(earlyItem);
		}

		assertThat(itemsSeen).containsExactly(oldItem, newItem);
	}

	private TraditionalEarlyItem createEarlyItem() {
		return mock(TraditionalEarlyItem.class);
	}
}

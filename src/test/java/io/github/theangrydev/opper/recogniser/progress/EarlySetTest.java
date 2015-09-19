package io.github.theangrydev.opper.recogniser.progress;

import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.recogniser.item.EarlyItem;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class EarlySetTest implements WithAssertions {

	@Test
	public void shouldBeAbleToIterateItemsThatWereAddedDuringTheSameIteration() {
		EarlyItem oldItem = createEarlyItem();
		EarlyItem newItem = createEarlyItem();

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

	private EarlyItem createEarlyItem() {
		return mock(EarlyItem.class);
	}
}

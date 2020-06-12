/*
 * Copyright 2015-2020 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.parser.early;

import io.github.theangrydev.opper.grammar.GrammarBuilder;
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

    private TraditionalEarlyItem createEarlyItem() {
        return mock(TraditionalEarlyItem.class);
    }
}

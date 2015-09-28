package io.github.theangrydev.opper.scanner.autonoma;

import com.google.common.collect.Multiset;

public class FrequencyBasedEncoder {

	public void relabel(Multiset<? extends Identifiable> frequencies) {
		int idSequence = 1;
		for (Multiset.Entry<? extends Identifiable> entry : frequencies.entrySet()) {
			entry.getElement().label(idSequence++);
		}
	}
}

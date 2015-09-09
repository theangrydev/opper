package io.github.theangrydev.opper.common;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {

	public static <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
}

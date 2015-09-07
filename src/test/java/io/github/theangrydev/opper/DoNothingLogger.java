package io.github.theangrydev.opper;

import java.util.function.Supplier;

public class DoNothingLogger implements Logger {

	@Override
	public void log(Supplier<String> message) {
		// do nothing
	}
}

package io.github.theangrydev.opper;

import io.github.theangrydev.opper.common.Logger;

import java.util.function.Supplier;

public class DoNothingLogger implements Logger {

	@Override
	public void log(Supplier<String> message) {
		// do nothing
	}
}

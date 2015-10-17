package io.github.theangrydev.opper.common;

import java.util.function.Supplier;

public class SystemOutLogger implements Logger {
	@Override
	public void log(Supplier<String> message) {
		System.out.println(message.get());
	}
}

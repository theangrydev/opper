package io.github.theangrydev.opper.common;

import java.util.function.Supplier;

public interface Logger {
	void log(Supplier<String> message);
}

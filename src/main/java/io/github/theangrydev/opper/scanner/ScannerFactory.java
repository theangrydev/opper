package io.github.theangrydev.opper.scanner;

import java.io.Reader;

public interface ScannerFactory {
	Scanner scanner(Reader charactersToParse);
}

package io.github.theangrydev.opper.parser;

import io.github.theangrydev.opper.scanner.Scanner;

public interface ParserFactory {
	Parser parser(Scanner scanner);
}

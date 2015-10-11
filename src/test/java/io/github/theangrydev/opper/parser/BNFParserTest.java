package io.github.theangrydev.opper.parser;

import com.google.common.base.Splitter;
import com.googlecode.yatspec.junit.Row;
import com.googlecode.yatspec.junit.Table;
import com.googlecode.yatspec.junit.TableRunner;
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.scanner.FixedScanner;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import io.github.theangrydev.opper.scanner.Scanner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.lang.Boolean.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(TableRunner.class)
public class BNFParserTest {

	@Table({
		@Row({"( ant )", "true"}),
		@Row({"ant", "false"}),
		@Row({"( ant", "false"}),
		@Row({"ant )", "false"}),
		@Row({"( ant , bat )", "true"}),
		@Row({"( ant , bat", "false"}),
		@Row({"( ( ant )", "false"}),
		@Row({"( ( ant ) )", "true"}),
		@Row({"( ( ant , bat ) , cow )", "true"}),
	})
	@Test
	public void shouldRecogniseABNFGrammar(String spaceSeperatedCorpus, String shouldParse) {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("ACCEPT")
			.withStartSymbol("TREE")
			.withRule("TREE", "(", "LIST", ")")
			.withRule("LIST", "THING")
			.withRule("LIST", "LIST", ",", "THING")
			.withRule("THING", "TREE")
			.withRule("THING", "NAME")
			.withRule("NAME", "ant")
			.withRule("NAME", "bat")
			.withRule("NAME", "cow")
			.build();
		Scanner scanner = FixedScanner.scanner(grammar, Splitter.on(' ').split(spaceSeperatedCorpus));

		Parser parser = new EarlyParser(new DoNothingLogger(), grammar, scanner);

		assertThat(parser.parse().isPresent()).isEqualTo(valueOf(shouldParse));
	}
}

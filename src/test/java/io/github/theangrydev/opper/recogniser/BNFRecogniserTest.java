package io.github.theangrydev.opper.recogniser;

import com.google.common.base.Splitter;
import com.googlecode.yatspec.junit.Row;
import com.googlecode.yatspec.junit.Table;
import com.googlecode.yatspec.junit.TableRunner;
import io.github.theangrydev.opper.common.DoNothingLogger;
import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Grammar;
import io.github.theangrydev.opper.grammar.GrammarBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.github.theangrydev.opper.corpus.FixedCorpus.corpus;
import static java.lang.Boolean.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(TableRunner.class)
public class BNFRecogniserTest {

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
		Corpus corpus = corpus(grammar, Splitter.on(' ').split(spaceSeperatedCorpus));

		Recogniser recogniser = new Recogniser(new DoNothingLogger(), grammar, corpus);

		assertThat(recogniser.parse().isPresent()).isEqualTo(valueOf(shouldParse));
	}
}

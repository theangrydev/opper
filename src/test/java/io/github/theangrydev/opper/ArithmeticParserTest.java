package io.github.theangrydev.opper;

import org.junit.Test;

import static io.github.theangrydev.opper.FixedCorpus.corpus;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * <pre>
 * <P> ::= <S> # the start rule
 * <S> ::= <S> "+" <M> | <M>
 * <M> ::= <M> "*" <T> | <T>
 * <T> ::= "1" | "2" | "3" | "4"
 * </pre>
 * @see <a href="https://en.wikipedia.org/w/index.php?title=Earley_parser&oldid=667926718#Example">Early parser example</a>
 */
public class ArithmeticParserTest {

	@Test
	public void shouldParseALeftRecursiveGrammar() {
		Grammar grammar = new GrammarBuilder()
			.withAcceptanceSymbol("P")
			.withStartSymbol("S")
			.withSymbols("+", "*", "M", "T", "1", "2", "3", "4")
			.withRule("S", "S", "+", "M")
			.withRule("S", "M")
			.withRule("M", "M", "*", "T")
			.withRule("M", "T")
			.withRule("T", "1")
			.withRule("T", "2")
			.withRule("T", "3")
			.withRule("T", "4")
			.build();

		Corpus corpus = corpus(grammar, "2", "+", "3", "+", "2", "+", "3", "*", "4");

		Parser parser = new Parser(new DoNothingLogger(), grammar, corpus);

		assertThat(parser.parse()).isTrue();
	}
}

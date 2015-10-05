package io.github.theangrydev.opper.recogniser.item;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySet;
import io.github.theangrydev.opper.recogniser.transition.TransitionsEarlySetsBySymbol;

public abstract class EarlyItem {
	protected final ParseTree parseTree;
	protected final TransitionsEarlySetsBySymbol origin;
	protected final DottedRule dottedRule;

	protected EarlyItem(ParseTree parseTree, TransitionsEarlySetsBySymbol origin, DottedRule dottedRule) {
		this.origin = origin;
		this.dottedRule = dottedRule;
		this.parseTree = parseTree;
	}

	protected EarlyItem(TransitionsEarlySetsBySymbol origin, DottedRule dottedRule) {
		this(new ParseTree(dottedRule.rule()), origin, dottedRule);
	}

	public DottedRule dottedRule() {
		return dottedRule;
	}

	public TransitionsEarlySetsBySymbol origin() {
		return origin;
	}

	public TransitionsEarlySet itemsThatCanAdvanceWhenThisIsComplete() {
		return origin.itemsThatCanAdvanceGiven(dottedRule.trigger());
	}

	public boolean hasCompletedAcceptanceRule(TransitionsEarlySetsBySymbol initialTransitions, Symbol acceptanceSymbol) {
		return origin == initialTransitions && dottedRule.isCompletedAcceptanceRule(acceptanceSymbol);
	}

	public boolean isComplete() {
		return dottedRule.isComplete();
	}

	public Symbol postDot() {
		return dottedRule.postDot();
	}

	public ParseTree parseTree() {
		return parseTree;
	}

	protected abstract EarlyItem advance();

	public EarlyItem advance(String content) {
		EarlyItem advance = advance();
		advance.parseTree.withContent(content);
		return advance;
	}

	public EarlyItem advance(EarlyItem earlyItem) {
		EarlyItem advance = advance();
		advance.parseTree.withChild(earlyItem.parseTree);
		return advance;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		final EarlyItem other = (EarlyItem) object;
		return this.origin == other.origin && this.dottedRule == other.dottedRule;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + dottedRule.toString() + " | " + parseTree;
	}
}

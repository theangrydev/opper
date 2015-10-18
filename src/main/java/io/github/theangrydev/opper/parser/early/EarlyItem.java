package io.github.theangrydev.opper.parser.early;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.parser.tree.ParseTreeNode;
import io.github.theangrydev.opper.scanner.Location;

import static io.github.theangrydev.opper.parser.tree.ParseTreeNode.node;

public abstract class EarlyItem {
	protected final ParseTreeNode parseTree;
	protected final TransitionsEarlySetsBySymbol origin;
	protected final DottedRule dottedRule;

	protected EarlyItem(ParseTreeNode parseTree, TransitionsEarlySetsBySymbol origin, DottedRule dottedRule) {
		this.origin = origin;
		this.dottedRule = dottedRule;
		this.parseTree = parseTree;
	}

	protected EarlyItem(TransitionsEarlySetsBySymbol origin, DottedRule dottedRule) {
		this(node(dottedRule.rule()), origin, dottedRule);
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

	public ParseTreeNode parseTree() {
		return parseTree;
	}

	protected abstract EarlyItem advance();

	public EarlyItem advance(String content, Location location) {
		EarlyItem advance = advance();
		advance.parseTree.withContent(content, location);
		return advance;
	}

	public EarlyItem advance(EarlyItem earlyItem) {
		EarlyItem advance = advance();
		advance.parseTree.withChild(earlyItem.parseTree);
		return advance;
	}

	public boolean sameAs(EarlyItem other) {
		return this == other || this.origin == other.origin && this.dottedRule == other.dottedRule;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + dottedRule.toString() + " | " + parseTree;
	}
}

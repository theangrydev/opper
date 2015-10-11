package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.List;

public class BinaryExpressionAnalyser<Result, Left, Right> extends ParseTreeNodeAnalyser<Result> {

	private final BinaryConstructor<Result, Left, Right> binaryConstructor;
	private final ParseTreeAnalyser<Left> leftAnalyser;
	private final ParseTreeAnalyser<Right> rightAnalyser;

	private BinaryExpressionAnalyser(BinaryConstructor<Result, Left, Right> binaryConstructor, ParseTreeAnalyser<Left> leftAnalyser, ParseTreeAnalyser<Right> rightAnalyser) {
		this.binaryConstructor = binaryConstructor;
		this.leftAnalyser = leftAnalyser;
		this.rightAnalyser = rightAnalyser;
	}

	public static <Result, Left, Right> BinaryExpressionAnalyser<Result, Left, Right> binaryExpression(BinaryConstructor<Result, Left, Right> binaryConstructor, ParseTreeAnalyser<Left> leftAnalyser, ParseTreeAnalyser<Right> rightAnalyser) {
		return new BinaryExpressionAnalyser<>(binaryConstructor, leftAnalyser, rightAnalyser);
	}

	public static <Result, Argument> BinaryExpressionAnalyser<Result, Argument, Argument> binaryExpression(BinaryConstructor<Result, Argument, Argument> binaryConstructor, ParseTreeAnalyser<Argument> argumentAnalyser) {
		return binaryExpression(binaryConstructor, argumentAnalyser, argumentAnalyser);
	}

	@Override
	protected final Result analyse(List<ParseTree> children) {
		return binaryConstructor.construct(leftAnalyser.analyse(children.get(0)), rightAnalyser.analyse(children.get(1)));
	}

	@FunctionalInterface
	interface BinaryConstructor<Result, Left, Right> {
		Result construct(Left left, Right right);
	}
}

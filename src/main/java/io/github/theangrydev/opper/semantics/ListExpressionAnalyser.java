package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.parser.tree.ParseTree;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ListExpressionAnalyser<Result, Argument> implements ParseTreeNodeAnalyser.NodeAnalyser<Result> {

	private final ListConstructor<Result, Argument> listConstructor;
	private final ParseTreeAnalyser<Argument> argumentAnalyser;

	private ListExpressionAnalyser(ListConstructor<Result, Argument> listConstructor, ParseTreeAnalyser<Argument> argumentAnalyser) {
		this.listConstructor = listConstructor;
		this.argumentAnalyser = argumentAnalyser;
	}

	public static <Result, Argument> ListExpressionAnalyser<Result, Argument> listExpression(ListConstructor<Result, Argument> listConstructor, ParseTreeAnalyser<Argument> argumentAnalyser) {
		return new ListExpressionAnalyser<>(listConstructor, argumentAnalyser);
	}

	@Override
	public final Result analyse(List<ParseTree> children) {
		List<Argument> arguments = children.stream().map(argumentAnalyser::analyse).collect(toList());
		return listConstructor.construct(arguments);
	}

	@FunctionalInterface
	public interface ListConstructor<Result, Argument> {
		Result construct(List<Argument> arguments);
	}
}

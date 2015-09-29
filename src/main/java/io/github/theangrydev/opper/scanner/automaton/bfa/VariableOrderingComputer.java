package io.github.theangrydev.opper.scanner.automaton.bfa;

import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.math.DoubleMath.log2;
import static io.github.theangrydev.opper.common.Predicates.not;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class VariableOrderingComputer {

	public static VariableOrdering determineOrdering(VariableSummary variableSummary, TransitionTable transitionTable) {
		int bitsPerRow = variableSummary.bitsPerRow();
		IntSet remainingVariableIds = variableSummary.allVariableIds();
		List<VariableOrder> variableOrders = new ArrayList<>(bitsPerRow);
		List<TransitionTable> frontier = singletonList(transitionTable);
		for (int height = 0; height < bitsPerRow; height++) {
			int countPerSplit = 1 << (bitsPerRow - height - 1);
			int nextVariable = determineNext(frontier, remainingVariableIds, countPerSplit);
			remainingVariableIds.remove(nextVariable);
			variableOrders.add(new VariableOrder(height, nextVariable));
			frontier = nextFrontier(frontier, nextVariable);
		}
		DecisionTree tree = DecisionTree.from(transitionTable, variableOrders);
		System.out.println("nodes=" + tree.count());
		return new VariableOrdering(variableSummary, variableOrders);
	}

	private static List<TransitionTable> nextFrontier(List<TransitionTable> frontier, int nextVariable) {
		Stream<TransitionTable> rowsWithVariable = frontier.stream().map(node -> node.rowsWithVariable(nextVariable));
		Stream<TransitionTable> rowsWithoutVariable = frontier.stream().map(node -> node.rowsWithoutVariable(nextVariable));
		return concat(rowsWithVariable, rowsWithoutVariable).filter(not(TransitionTable::isEmpty)).collect(toList());
	}

	private static int determineNext(List<TransitionTable> frontier, IntSet remainingVariableIds, int countPerSplit) {
		double maxEntropy = -Double.MAX_VALUE;
		int maxVariable = remainingVariableIds.iterator().nextInt();
		for (int variable : remainingVariableIds) {
			double entropy = frontierEntropy(frontier, variable, countPerSplit);
			if (entropy > maxEntropy) {
				maxEntropy = entropy;
				maxVariable = variable;
			}
		}
		return maxVariable;
	}

	private static double frontierEntropy(List<TransitionTable> frontier, int variable, int countPerSplit) {
		return frontier.stream().mapToDouble(node -> nodeEntropy(node, variable, countPerSplit)).sum();
	}

	private static double nodeEntropy(TransitionTable node, int variable, int countPerSplit) {
		int rowsWithVariable = node.numberOfRowsWithVariable(variable);
		int rowsWithoutVariable = node.size() - rowsWithVariable;
		return entropy(rowsWithVariable, countPerSplit) + entropy(rowsWithoutVariable, countPerSplit);
	}

	private static double entropy(int positiveExamples, int countPerSplit) {
		if (positiveExamples == 0) {
			return 0.0d;
		}
		int negativeExamples = countPerSplit - positiveExamples;
		double positiveProportion = (double) positiveExamples / countPerSplit;
		double negativeProportion = (double) negativeExamples / countPerSplit;
		return positiveProportion * log2(positiveProportion) + negativeProportion * log2(negativeProportion);
	}

	public static class DecisionTree {
		private TransitionTable examples;
		private DecisionTree zero;
		private DecisionTree one;

		public static DecisionTree from(TransitionTable transitionTable, List<VariableOrder> variableOrderOrdering) {
			DecisionTree root = new DecisionTree(transitionTable);
			List<DecisionTree> trees = new ArrayList<>();
			trees.add(root);
			List<DecisionTree> nextTrees = new ArrayList<>();
			for (VariableOrder variableOrder : variableOrderOrdering) {
				for (DecisionTree tree : trees) {
					if (tree.examples.isEmpty()) {
						continue;
					}
					DecisionTree one = new DecisionTree(tree.examples.rowsWithVariable(variableOrder.id()));
					DecisionTree zero = new DecisionTree(tree.examples.rowsWithoutVariable(variableOrder.id()));
					tree.one = one;
					tree.zero = zero;
					nextTrees.add(one);
					nextTrees.add(zero);
				}
				trees = nextTrees;
				nextTrees = new ArrayList<>();
			}
			return root;
		}

		private DecisionTree(TransitionTable examples) {
			this.examples = examples;
		}

		public int count() {
			int count = 1;
			if (zero != null) {
				count += zero.count();
			}
			if (one != null) {
				count += one.count();
			}
			return count;
		}
	}
}

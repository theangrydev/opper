package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.scanner.autonoma.TransitionTable;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.math.DoubleMath.log2;
import static io.github.theangrydev.opper.common.Predicates.not;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class VariableOrderingCalculator {

	public List<Variable> determineOrdering(int bitsPerRow, TransitionTable transitionTable) {
		List<Variable> variables = new ArrayList<>(bitsPerRow);
		IntSet remainingVariables = allVariables(bitsPerRow);
		List<TransitionTable> frontier = singletonList(transitionTable);
		for (int height = 0; height < bitsPerRow; height++) {
			int countPerSplit = 1 << (bitsPerRow - height - 1);
			int nextVariable = determineNext(frontier, remainingVariables, countPerSplit);
			remainingVariables.remove(nextVariable);
			variables.add(new Variable(height, nextVariable));
			frontier = nextFrontier(frontier, nextVariable);
		}
		DecisionTree tree = DecisionTree.from(transitionTable, variables);
		System.out.println("nodes=" + tree.count());
		return variables;
	}

	public static class DecisionTree {
		private TransitionTable examples;
		private DecisionTree zero;
		private DecisionTree one;

		public static DecisionTree from(TransitionTable transitionTable, List<Variable> variableOrdering) {
			DecisionTree root = new DecisionTree(transitionTable);
			List<DecisionTree> trees = new ArrayList<>();
			trees.add(root);
			List<DecisionTree> nextTrees = new ArrayList<>();
			for (Variable variable : variableOrdering) {
				for (DecisionTree tree : trees) {
					if (tree.examples.isEmpty()) {
						continue;
					}
					DecisionTree one = new DecisionTree(tree.examples.rowsWithVariable(variable.id()));
					DecisionTree zero = new DecisionTree(tree.examples.rowsWithoutVariable(variable.id()));
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

	private List<TransitionTable> nextFrontier(List<TransitionTable> frontier, int nextVariable) {
		Stream<TransitionTable> rowsWithVariable = frontier.stream().map(node -> node.rowsWithVariable(nextVariable));
		Stream<TransitionTable> rowsWithoutVariable = frontier.stream().map(node -> node.rowsWithoutVariable(nextVariable));
		return concat(rowsWithVariable, rowsWithoutVariable).filter(not(TransitionTable::isEmpty)).collect(toList());
	}

	private IntSet allVariables(int bitsPerRow) {
		IntSet allVariables = new IntOpenHashSet(bitsPerRow);
		for (int i = 1; i <= bitsPerRow; i++) {
			allVariables.add(i);
		}
		return allVariables;
	}

	private int determineNext(List<TransitionTable> frontier, IntSet remainingVariables, int countPerSplit) {
		double maxEntropy = -Double.MAX_VALUE;
		int maxVariable = remainingVariables.iterator().nextInt();
		for (int variable : remainingVariables) {
			double entropy = frontierEntropy(frontier, variable, countPerSplit);
			if (entropy > maxEntropy) {
				maxEntropy = entropy;
				maxVariable = variable;
			}
		}
		return maxVariable;
	}

	private double frontierEntropy(List<TransitionTable> frontier, int variable, int countPerSplit) {
		return frontier.stream().mapToDouble(node -> nodeEntropy(node, variable, countPerSplit)).sum();
	}

	private double nodeEntropy(TransitionTable node, int variable, int countPerSplit) {
		int rowsWithVariable = node.numberOfRowsWithVariable(variable);
		int rowsWithoutVariable = node.size() - rowsWithVariable;
		return entropy(rowsWithVariable, countPerSplit) + entropy(rowsWithoutVariable, countPerSplit);
	}

	private double entropy(int positiveExamples, int countPerSplit) {
		if (positiveExamples == 0) {
			return 0.0d;
		}
		int negativeExamples = countPerSplit - positiveExamples;
		double positiveProportion = (double) positiveExamples / countPerSplit;
		double negativeProportion = (double) negativeExamples / countPerSplit;
		return positiveProportion * log2(positiveProportion) + negativeProportion * log2(negativeProportion);
	}

}

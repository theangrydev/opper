package io.github.theangrydev.opper.scanner.autonoma;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.math.DoubleMath.log2;
import static io.github.theangrydev.opper.common.Predicates.not;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class VariableOrderingCalculator {

	public List<Variable> determineOrdering(int bitsPerRow, List<BitSet> transitionTable) {
		List<Variable> variables = new ArrayList<>(bitsPerRow);
		IntSet remainingVariables = allVariables(bitsPerRow);
		List<List<BitSet>> frontier = singletonList(transitionTable);
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
		private List<BitSet> examples;
		private DecisionTree zero;
		private DecisionTree one;

		public static DecisionTree from(List<BitSet> transitionTable, List<Variable> variableOrdering) {
			DecisionTree root = new DecisionTree(transitionTable);
			List<DecisionTree> trees = new ArrayList<>();
			trees.add(root);
			List<DecisionTree> nextTrees = new ArrayList<>();
			for (Variable variable : variableOrdering) {
				for (DecisionTree tree : trees) {
					if (tree.examples.isEmpty()) {
						continue;
					}
					DecisionTree one = new DecisionTree(rowsWithVariable(tree.examples, variable.id()));
					DecisionTree zero = new DecisionTree(rowsWithoutVariable(tree.examples, variable.id()));
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

		private DecisionTree(List<BitSet> examples) {
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

	private List<List<BitSet>> nextFrontier(List<List<BitSet>> frontier, int nextVariable) {
		Stream<List<BitSet>> rowsWithVariable = frontier.stream().map(node -> rowsWithVariable(node, nextVariable));
		Stream<List<BitSet>> rowsWithoutVariable = frontier.stream().map(node -> rowsWithoutVariable(node, nextVariable));
		return concat(rowsWithVariable, rowsWithoutVariable).filter(not(List::isEmpty)).collect(toList());
	}

	private IntSet allVariables(int bitsPerRow) {
		IntSet allVariables = new IntOpenHashSet(bitsPerRow);
		for (int i = 1; i <= bitsPerRow; i++) {
			allVariables.add(i);
		}
		return allVariables;
	}

	private int determineNext(List<List<BitSet>> frontier, IntSet remainingVariables, int countPerSplit) {
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

	private double frontierEntropy(List<List<BitSet>> frontier, int variable, int countPerSplit) {
		return frontier.stream().mapToDouble(node -> nodeEntropy(node, variable, countPerSplit)).sum();
	}

	private double nodeEntropy(List<BitSet> node, int variable, int countPerSplit) {
		int rowsWithVariable = numberOfRowsWithVariable(node, variable);
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

	private int numberOfRowsWithVariable(List<BitSet> node, int variable) {
		return rowsWithVariable(node, variable).size();
	}

	private static List<BitSet> rowsWithVariable(List<BitSet> node, int variable) {
		return node.stream().filter(row -> row.get(variable - 1)).collect(toList());
	}

	private static List<BitSet> rowsWithoutVariable(List<BitSet> node, int variable) {
		return node.stream().filter(row -> !row.get(variable - 1)).collect(toList());
	}
}

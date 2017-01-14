/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
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

/**
 * The original variable ordering can be reordered to optimize the size of the BDDs that will be expressed in terms
 * of those variables.
 * <p>
 * Each row in the transition table uses {@link VariableSummary#bitsPerRow()} bits to represent which from/to/transition
 * variables are present, encoded as F | C | T where F is the from id, C is the transition id and T is the character id.
 * <p>
 * Each bit within the encoded value corresponds to a BDD variable.
 * <p>
 * The heuristic implemented here aims to order the BDD variables according to how much discriminatory power each
 * variable provides in the transition table.
 * <p>
 * For example, if partitioning by a variable being present or not amongst all the elements of the transition table
 * results in a 50/50 split, this indicates that this variable has bad discriminatory power, no better than guessing.
 * This makes it a bad candidate for an early variable, since it will not cut down the total number of
 * nodes needed to represent the transition table.
 * Conversely, if splitting results in a 99/1 split, this indicates very good discriminatory power, then it is a good
 * candidate for splitting, since it should reduce the total number of nodes.
 */
public class VariableOrderingComputer {

    public static VariableOrdering determineOrdering(VariableSummary variableSummary, TransitionTable transitionTable) {
        int bitsPerRow = variableSummary.bitsPerRow();
        IntSet remainingVariableIds = variableSummary.allVariableIds();
        List<Variable> variablesWithOrder = new ArrayList<>(bitsPerRow);
        List<TransitionTable> frontier = singletonList(transitionTable);
        for (int height = 0; height < bitsPerRow; height++) {
            int countPerSplit = countPerSplit(bitsPerRow, height);
            int nextVariable = determineNext(frontier, remainingVariableIds, countPerSplit);
            remainingVariableIds.remove(nextVariable);
            variablesWithOrder.add(new Variable(height, nextVariable));
            frontier = nextFrontier(frontier, nextVariable);
        }
        return new VariableOrdering(variableSummary, variablesWithOrder);
    }

    private static int countPerSplit(int bitsPerRow, int height) {
        return 1 << (bitsPerRow - height - 1);
    }

    private static List<TransitionTable> nextFrontier(List<TransitionTable> frontier, int nextVariable) {
        Stream<TransitionTable> rowsWithVariable = frontier.stream().map(node -> node.rowsWithVariable(nextVariable));
        Stream<TransitionTable> rowsWithoutVariable = frontier.stream().map(node -> node.rowsWithoutVariable(nextVariable));
        return concat(rowsWithVariable, rowsWithoutVariable).filter(not(TransitionTable::isEmpty)).collect(toList());
    }

    private static int determineNext(List<TransitionTable> frontier, IntSet remainingVariableIds, int countPerSplit) {
        double minEntropy = Double.MAX_VALUE;
        int minVariable = remainingVariableIds.iterator().nextInt();
        for (int variable : remainingVariableIds) {
            double entropy = frontierEntropy(frontier, variable, countPerSplit);
            if (entropy < minEntropy) {
                minEntropy = entropy;
                minVariable = variable;
            }
        }
        return minVariable;
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
        return -(positiveProportion * log2(positiveProportion) + negativeProportion * log2(negativeProportion));
    }
}

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
}

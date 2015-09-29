package io.github.theangrydev.opper.scanner.bdd;

import java.util.Arrays;
import java.util.stream.IntStream;

public class BDDVariableAssignment {
	private final int[] assignedIndexes;

	public BDDVariableAssignment(int[] assignment) {
		this.assignedIndexes = IntStream.range(0, assignment.length).filter(index -> assignment[index] == 1).toArray();
	}

	@Override
	public String toString() {
		return "assigned: " + Arrays.toString(assignedIndexes);
	}

	public IntStream assignedIndexes() {
		return Arrays.stream(assignedIndexes);
	}
}

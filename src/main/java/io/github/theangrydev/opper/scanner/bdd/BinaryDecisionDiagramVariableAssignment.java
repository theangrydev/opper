package io.github.theangrydev.opper.scanner.bdd;

import java.util.Arrays;

public class BinaryDecisionDiagramVariableAssignment {
	private final int[] assignment;

	public BinaryDecisionDiagramVariableAssignment(int[] assignment) {
		this.assignment = assignment;
	}

	@Override
	public String toString() {
		return "assigned: " + Arrays.toString(assignment);
	}

	public int[] assignment() {
		return assignment;
	}
}

package io.github.theangrydev.opper.scanner.bdd;

import jdd.bdd.BDD;
import jdd.bdd.Permutation;

public class BinaryDecisionDiagram {

	private final BDD bdd;
	private final int id;

	private BinaryDecisionDiagram(BDD bdd, int id) {
		this.bdd = bdd;
		this.id = bdd.ref(id);
	}

	public static BinaryDecisionDiagram newVariable(BDD bdd) {
		return new BinaryDecisionDiagram(bdd, bdd.createVar());
	}

	public static BinaryDecisionDiagram anything(BDD bdd) {
		return new BinaryDecisionDiagram(bdd, bdd.getOne());
	}

	public static BinaryDecisionDiagram nothing(BDD bdd) {
		return new BinaryDecisionDiagram(bdd, bdd.getZero());
	}

	public static BinaryDecisionDiagram newCube(BDD bdd, boolean[] setVariables) {
		return new BinaryDecisionDiagram(bdd, bdd.cube(setVariables));
	}

	public int id() {
		return id;
	}

	public BinaryDecisionDiagram not() {
		return new BinaryDecisionDiagram(bdd, bdd.not(id));
	}

	public BinaryDecisionDiagram andTo(BinaryDecisionDiagram binaryDecisionDiagram) {
		return new BinaryDecisionDiagram(bdd, bdd.andTo(id, binaryDecisionDiagram.id));
	}

	public BinaryDecisionDiagram orTo(BinaryDecisionDiagram binaryDecisionDiagram) {
		return new BinaryDecisionDiagram(bdd, bdd.orTo(id, binaryDecisionDiagram.id));
	}

	public void printSet() {
		bdd.printSet(id);
	}

	public BinaryDecisionDiagram and(BinaryDecisionDiagram binaryDecisionDiagram) {
		return new BinaryDecisionDiagram(bdd, bdd.and(id, binaryDecisionDiagram.id));
	}

	public boolean isNotZero() {
		return id != bdd.getZero();
	}

	public BinaryDecisionDiagramVariableAssignment oneSatisfyingAssignment() {
		return new BinaryDecisionDiagramVariableAssignment(bdd.oneSat(id, null));
	}

	public void discard() {
		bdd.deref(id);
	}

	public BinaryDecisionDiagram replaceTo(Permutation permutation) {
		BinaryDecisionDiagram result = new BinaryDecisionDiagram(bdd, bdd.replace(id, permutation));
		discard();
		return result;
	}

	public BinaryDecisionDiagram existsTo(BinaryDecisionDiagram binaryDecisionDiagram) {
		BinaryDecisionDiagram result = new BinaryDecisionDiagram(bdd, bdd.exists(id, binaryDecisionDiagram.id));
		discard();
		return result;
	}
}

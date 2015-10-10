package io.github.theangrydev.opper.scanner.bdd;

import jdd.bdd.BDD;
import jdd.bdd.Permutation;

public class BinaryDecisionDiagram {

	private final BDD bdd;
	private int id;

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
		this.id = bdd.andTo(id, binaryDecisionDiagram.id);
		return this;
	}

	public BinaryDecisionDiagram orTo(BinaryDecisionDiagram binaryDecisionDiagram) {
		this.id = bdd.orTo(id, binaryDecisionDiagram.id);
		return this;
	}

	public void printSet() {
		bdd.printSet(id);
	}

	public BinaryDecisionDiagram and(BinaryDecisionDiagram binaryDecisionDiagram, BinaryDecisionDiagram buffer) {
		buffer.discard();
		buffer.id = bdd.ref(bdd.and(id, binaryDecisionDiagram.id));
		return buffer;
	}

	public boolean isNotZero() {
		return id != bdd.getZero();
	}

	public int[] oneSatisfyingAssignment(int[] buffer) {
		return bdd.oneSat(id, buffer);
	}

	private void discard() {
		discard(id);
	}

	private void discard(int id) {
		bdd.deref(id);
	}

	public BinaryDecisionDiagram replaceTo(Permutation permutation) {
		int oldId = id;
		this.id = bdd.replace(id, permutation);
		discard(oldId);
		return this;
	}

	public BinaryDecisionDiagram existsTo(BinaryDecisionDiagram binaryDecisionDiagram) {
		int oldId = id;
		this.id = bdd.exists(id, binaryDecisionDiagram.id);
		discard(oldId);
		return this;
	}

	public BinaryDecisionDiagram relativeProductTo(BinaryDecisionDiagram transitions, BinaryDecisionDiagram existsFromStateAndCharacter) {
		int oldId = id;
		this.id = bdd.relProd(id, transitions.id, existsFromStateAndCharacter.id);
		discard(oldId);
		return this;
	}

	public BinaryDecisionDiagram copy() {
		return new BinaryDecisionDiagram(bdd, bdd.and(bdd.getOne(), id));
	}
}

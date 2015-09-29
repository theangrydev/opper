package io.github.theangrydev.opper.scanner.bdd;

import jdd.bdd.BDD;
import jdd.bdd.Permutation;

public class BDDVariable {

	private final BDD bdd;
	private final int id;

	private BDDVariable(BDD bdd, int id) {
		this.bdd = bdd;
		this.id = bdd.ref(id);
	}

	public static BDDVariable newVariable(BDD bdd) {
		return new BDDVariable(bdd, bdd.createVar());
	}

	public static BDDVariable newCube(BDD bdd, boolean[] setVariables) {
		return new BDDVariable(bdd, bdd.cube(setVariables));
	}

	public int id() {
		return id;
	}

	public BDDVariable not() {
		return new BDDVariable(bdd, bdd.not(id));
	}

	public BDDVariable andTo(BDDVariable bddVariable) {
		return new BDDVariable(bdd, bdd.andTo(id, bddVariable.id));
	}

	public BDDVariable orTo(BDDVariable bddVariable) {
		return new BDDVariable(bdd, bdd.orTo(id, bddVariable.id));
	}

	public void printSet() {
		bdd.printSet(id);
	}

	public BDDVariable and(BDDVariable bddVariable) {
		return new BDDVariable(bdd, bdd.and(id, bddVariable.id));
	}

	public boolean isZero() {
		return id != bdd.getZero();
	}

	public int[] oneSatisfyingAssignment() {
		return bdd.oneSat(id, null);
	}

	public void discard() {
		bdd.deref(id);
	}

	public BDDVariable replaceTo(Permutation permutation) {
		BDDVariable result = new BDDVariable(bdd, bdd.replace(id, permutation));
		discard();
		return result;
	}

	public BDDVariable existsTo(BDDVariable bddVariable) {
		BDDVariable result = new BDDVariable(bdd, bdd.exists(id, bddVariable.id));
		discard();
		return result;
	}
}

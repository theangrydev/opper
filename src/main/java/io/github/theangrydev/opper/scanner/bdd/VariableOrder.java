package io.github.theangrydev.opper.scanner.bdd;

public class VariableOrder {
	private final int order;
	private final int id;

	public VariableOrder(int order, int id) {
		this.order = order;
		this.id = id;
	}

	public int order() {
		return order;
	}

	public int id() {
		return id;
	}

	@Override
	public String toString() {
		return "(" + order + "," + id + ")";
	}
}
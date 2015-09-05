package io.github.theangrydev.opper;

public class Rule {

	private final int index;
	private final Symbol left;
	private final String right;

	public Rule(int index, Symbol left, String right) {
		this.index = index;
		this.left = left;
		this.right = right;
	}

	public int length() {
		return right.length();
	}

	public Symbol symbolAt(int dotPosition) {
		return right.symbolAt(dotPosition);
	}

	public Symbol left() {
		return left;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Rule rule = (Rule) o;
		return index == rule.index && !(left != null ? !left.equals(rule.left) : rule.left != null) && !(right != null ? !right.equals(rule.right) : rule.right != null);
	}

	@Override
	public int hashCode() {
		return index;
	}

	@Override
	public java.lang.String toString() {
		return left + " -> " + right;
	}

}

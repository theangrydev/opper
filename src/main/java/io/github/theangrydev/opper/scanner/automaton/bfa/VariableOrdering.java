package io.github.theangrydev.opper.scanner.automaton.bfa;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class VariableOrdering {
	private final VariableSummary variableSummary;
	private final List<Variable> variablesWithOrder;

	public VariableOrdering(VariableSummary variableSummary, List<Variable> variablesWithOrder) {
		this.variableSummary = variableSummary;
		this.variablesWithOrder = variablesWithOrder;
	}

	public int variableId(int order) {
		return variablesWithOrder.get(order).id();
	}

	public List<Variable> allVariables() {
		return variablesWithOrder;
	}

	public Stream<Variable> toStateVariablesInOriginalOrder() {
		return variablesWithOrder.stream().filter(variableSummary::isToState).sorted(comparing(Variable::id));
	}

	public Stream<Variable> fromStateVariablesInOriginalOrder() {
		return variablesWithOrder.stream().filter(variableSummary::isFromState).sorted(comparing(Variable::id));
	}

	public Stream<Variable> fromStateOrCharacterVariables() {
		return variablesWithOrder.stream().filter(variableSummary::isFromStateOrCharacter);
	}

	public Stream<Variable> fromStateVariables() {
		return variablesWithOrder.stream().filter(variableSummary::isFromState);
	}

	public int numberOfVariables() {
		return variablesWithOrder.size();
	}

	public Stream<Variable> toStateVariables() {
		return variablesWithOrder.stream().filter(variableSummary::isToState);
	}

	public Stream<Variable> characterVariables() {
		return variablesWithOrder.stream().filter(variableSummary::isCharacter);
	}
}

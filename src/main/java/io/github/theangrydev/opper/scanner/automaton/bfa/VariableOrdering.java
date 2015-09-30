package io.github.theangrydev.opper.scanner.automaton.bfa;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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

	public List<Variable> fromStateVariables() {
		return variablesWithOrder.stream().filter(variableSummary::isFromState).collect(toList());
	}

	public int numberOfVariables() {
		return variablesWithOrder.size();
	}

	public List<Variable> toStateVariables() {
		return variablesWithOrder.stream().filter(variableSummary::isToState).collect(toList());
	}

	public List<Variable> characterVariables() {
		return variablesWithOrder.stream().filter(variableSummary::isCharacter).collect(toList());
	}
}

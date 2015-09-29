package io.github.theangrydev.opper.scanner.automaton.bfa;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class VariableOrdering {
	private final VariableSummary variableSummary;
	private final List<VariableOrder> variableOrders;

	public VariableOrdering(VariableSummary variableSummary, List<VariableOrder> variableOrders) {
		this.variableSummary = variableSummary;
		this.variableOrders = variableOrders;
	}

	public int id(int i) {
		return variableOrders.get(i).id();
	}

	public List<VariableOrder> allVariables() {
		return variableOrders;
	}

	public Stream<VariableOrder> toStateVariablesInOriginalOrder() {
		return variableOrders.stream().filter(variableSummary::isToState).sorted(comparing(VariableOrder::id));
	}

	public Stream<VariableOrder> fromStateVariablesInOriginalOrder() {
		return variableOrders.stream().filter(variableSummary::isFromState).sorted(comparing(VariableOrder::id));
	}

	public Stream<VariableOrder> fromStateOrCharacterVariables() {
		return variableOrders.stream().filter(variableSummary::isFromStateOrCharacter);
	}

	public Stream<VariableOrder> fromStateVariables() {
		return variableOrders.stream().filter(variableSummary::isFromState);
	}

	public int numberOfVariables() {
		return variableOrders.size();
	}

	public Stream<VariableOrder> toStateVariables() {
		return variableOrders.stream().filter(variableSummary::isToState);
	}

	public Stream<VariableOrder> characterVariables() {
		return variableOrders.stream().filter(variableSummary::isCharacter);
	}
}

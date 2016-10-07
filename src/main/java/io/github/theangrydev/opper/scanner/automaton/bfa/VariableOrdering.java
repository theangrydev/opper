/*
 * Copyright 2015 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
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

	public List<Variable> fromStateOrCharacterVariables() {
		return variablesWithOrder.stream().filter(variableSummary::isFromStateOrCharacter).collect(toList());
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

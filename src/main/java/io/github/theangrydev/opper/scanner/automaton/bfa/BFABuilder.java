package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramFactory;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BFABuilder {

	public static BFA convertToBFA(NFA nfa) {
		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);
		VariableSummary variableSummary = nfa.variableSummary();
		VariableOrdering variableOrdering = VariableOrderingComputer.determineOrdering(variableSummary, transitionTable);

		BinaryDecisionDiagramFactory binaryDecisionDiagramFactory = new BinaryDecisionDiagramFactory();
		BinaryDecisionDiagramVariables binaryDecisionDiagramVariables = new BinaryDecisionDiagramVariables(variableOrdering.numberOfVariables(), binaryDecisionDiagramFactory);
		BinaryDecisionDiagram startingFrom = fromState(variableOrdering, variableSummary, binaryDecisionDiagramVariables, nfa.initialState());
		BinaryDecisionDiagram transitionBddTable = computeTransitionTable(variableOrdering, binaryDecisionDiagramVariables, transitionTable);
		Char2ObjectMap<BinaryDecisionDiagram> characterBddSets = computeCharacterBddSets(variableOrdering, nfa.characterTransitions(), variableSummary, binaryDecisionDiagramVariables);
		BinaryDecisionDiagram acceptanceBddSet = computeAcceptanceSet(variableOrdering, nfa, variableSummary, binaryDecisionDiagramVariables);
		Permutation relabelToStateToFromState = relabelToStateToFromState(variableOrdering, binaryDecisionDiagramVariables, binaryDecisionDiagramFactory);
		BinaryDecisionDiagram existsFromStateAndCharacter = existsFromStateAndCharacter(variableOrdering, variableSummary, binaryDecisionDiagramFactory);
		List<Symbol> symbolsByStateId = nfa.symbolsByStateId();
		return new BFA(transitionBddTable, characterBddSets, acceptanceBddSet, startingFrom, variableOrdering, variableSummary, symbolsByStateId, relabelToStateToFromState, existsFromStateAndCharacter);
	}

	private static BinaryDecisionDiagram computeTransitionTable(VariableOrdering variableOrders, BinaryDecisionDiagramVariables binaryDecisionDiagramVariables, TransitionTable transitionTable) {
		List<SetVariables> transitions = transitionTable.transitions();
		BinaryDecisionDiagram bddDisjunction = bddRow(variableOrders.allVariables(), binaryDecisionDiagramVariables, transitions.get(0));
		for (int i = 1; i < transitions.size(); i++) {
			BinaryDecisionDiagram bddRow = bddRow(variableOrders.allVariables(), binaryDecisionDiagramVariables, transitions.get(i));
			bddDisjunction = bddDisjunction.orTo(bddRow);
		}
		return bddDisjunction;
	}

	private static BinaryDecisionDiagram bddRow(List<VariableOrder> variableOrders, BinaryDecisionDiagramVariables binaryDecisionDiagramVariables, SetVariables setVariables) {
		BinaryDecisionDiagram bddRow = setVariable(binaryDecisionDiagramVariables, setVariables, variableOrders.get(0));
		for (int i = 1; i < variableOrders.size(); i++) {
			BinaryDecisionDiagram binaryDecisionDiagram = setVariable(binaryDecisionDiagramVariables, setVariables, variableOrders.get(i));
			bddRow = bddRow.andTo(binaryDecisionDiagram);
		}
		return bddRow;
	}

	private static BinaryDecisionDiagram setVariable(BinaryDecisionDiagramVariables binaryDecisionDiagramVariables, SetVariables setVariables, VariableOrder variableOrder) {
		if (setVariables.contains(variableOrder)) {
			return binaryDecisionDiagramVariables.variable(variableOrder.order());
		} else {
			return binaryDecisionDiagramVariables.notVariable(variableOrder.order());
		}
	}

	private static Char2ObjectMap<BinaryDecisionDiagram> computeCharacterBddSets(VariableOrdering variableOrders, List<CharacterTransition> characterTransitions, VariableSummary variableSummary, BinaryDecisionDiagramVariables binaryDecisionDiagramVariables) {
		List<VariableOrder> characterVariableOrders = variableOrders.characterVariables().collect(toList());
		Char2ObjectMap<BinaryDecisionDiagram> characterBddSets = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			SetVariables character = SetVariables.character(variableSummary, characterTransition);
			BinaryDecisionDiagram bddRow = bddRow(characterVariableOrders, binaryDecisionDiagramVariables, character);
			characterBddSets.put(characterTransition.character(), bddRow);
		}
		return characterBddSets;
	}

	private static BinaryDecisionDiagram computeAcceptanceSet(VariableOrdering variableOrdering, NFA nfa, VariableSummary variableSummary, BinaryDecisionDiagramVariables binaryDecisionDiagramVariables) {
		List<State> acceptanceStates = nfa.acceptanceStates();
		List<VariableOrder> toStateVariableOrders = variableOrdering.toStateVariables().collect(toList());

		SetVariables firstToState = SetVariables.toState(variableSummary, acceptanceStates.get(0));
		BinaryDecisionDiagram bddDisjunction = bddRow(toStateVariableOrders, binaryDecisionDiagramVariables, firstToState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			State state = acceptanceStates.get(i);
			SetVariables toState = SetVariables.toState(variableSummary, state);
			BinaryDecisionDiagram bddRow = bddRow(toStateVariableOrders, binaryDecisionDiagramVariables, toState);
			bddDisjunction = bddDisjunction.orTo(bddRow);
		}
		return bddDisjunction;
	}

	private static BinaryDecisionDiagram fromState(VariableOrdering variableOrdering, VariableSummary variableSummary, BinaryDecisionDiagramVariables binaryDecisionDiagramVariables, State state) {
		List<VariableOrder> fromStateVariableOrders = variableOrdering.fromStateVariables().collect(toList());
		SetVariables fromState = SetVariables.fromState(variableSummary, state);
		return bddRow(fromStateVariableOrders, binaryDecisionDiagramVariables, fromState);
	}

	private static Permutation relabelToStateToFromState(VariableOrdering variableOrdering, BinaryDecisionDiagramVariables binaryDecisionDiagramVariables, BinaryDecisionDiagramFactory binaryDecisionDiagramFactory) {
		Stream<BinaryDecisionDiagram> toVariables = variableOrdering.toStateVariablesInOriginalOrder().map(binaryDecisionDiagramVariables::variable);
		Stream<BinaryDecisionDiagram> fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().map(binaryDecisionDiagramVariables::variable);
		return binaryDecisionDiagramFactory.createPermutation(toVariables, fromVariables);
	}

	private static BinaryDecisionDiagram existsFromStateAndCharacter(VariableOrdering variableOrdering, VariableSummary variableSummary, BinaryDecisionDiagramFactory variableFactory) {
		List<Integer> fromStateOrCharacterVariables = variableOrdering.fromStateOrCharacterVariables().map(VariableOrder::order).collect(toList());
		boolean[] presentVariables = variableSummary.presentVariables(fromStateOrCharacterVariables);
		return variableFactory.newCube(presentVariables);
	}
}

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
		AllVariables allVariables = new AllVariables(variableOrdering.numberOfVariables(), binaryDecisionDiagramFactory);

		SpecifyVariables specifyVariables = new SpecifyVariables(allVariables);

		BFAAcceptance bfaAcceptance = bfaAcceptance(nfa, variableSummary, variableOrdering, specifyVariables);

		BFATransitions bfaTransitions = bfaTransitions(nfa, transitionTable, variableSummary, variableOrdering, binaryDecisionDiagramFactory, specifyVariables);

		BinaryDecisionDiagram startingFrom = fromState(variableOrdering, variableSummary, specifyVariables, nfa.initialState());
		Permutation relabelToStateToFromState = relabelToStateToFromState(variableOrdering, allVariables, binaryDecisionDiagramFactory);

		return new BFA(bfaTransitions, bfaAcceptance, startingFrom, relabelToStateToFromState);
	}

	private static BFATransitions bfaTransitions(NFA nfa, TransitionTable transitionTable, VariableSummary variableSummary, VariableOrdering variableOrdering, BinaryDecisionDiagramFactory binaryDecisionDiagramFactory, SpecifyVariables specifyVariables) {
		BinaryDecisionDiagram transitions = transitions(variableOrdering, specifyVariables, transitionTable);
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = characterPresence(variableOrdering, nfa.characterTransitions(), variableSummary, specifyVariables);
		BinaryDecisionDiagram existsFromStateAndCharacter = existsFromStateAndCharacter(variableOrdering, variableSummary, binaryDecisionDiagramFactory);
		return new BFATransitions(transitions, characterPresences, existsFromStateAndCharacter);
	}

	private static BFAAcceptance bfaAcceptance(NFA nfa, VariableSummary variableSummary, VariableOrdering variableOrdering, SpecifyVariables specifyVariables) {
		BinaryDecisionDiagram acceptingStates = acceptingStates(variableOrdering, nfa, variableSummary, specifyVariables);
		List<Symbol> symbolsByStateId = nfa.symbolsByStateId();
		return new BFAAcceptance(acceptingStates, variableOrdering, variableSummary, symbolsByStateId);
	}

	private static BinaryDecisionDiagram transitions(VariableOrdering variableOrders, SpecifyVariables specifyVariables, TransitionTable transitionTable) {
		List<SetVariables> transitions = transitionTable.transitions();
		BinaryDecisionDiagram allTransitions = specifyVariables.specifyVariables(variableOrders.allVariables(), transitions.get(0));
		for (int i = 1; i < transitions.size(); i++) {
			BinaryDecisionDiagram transition = specifyVariables.specifyVariables(variableOrders.allVariables(), transitions.get(i));
			allTransitions = allTransitions.orTo(transition);
		}
		return allTransitions;
	}

	private static Char2ObjectMap<BinaryDecisionDiagram> characterPresence(VariableOrdering variableOrders, List<CharacterTransition> characterTransitions, VariableSummary variableSummary, SpecifyVariables specifyVariables) {
		List<Variable> characterVariables = variableOrders.characterVariables().collect(toList());
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			SetVariables character = SetVariables.character(variableSummary, characterTransition);
			BinaryDecisionDiagram characterPresence = specifyVariables.specifyVariables(characterVariables, character);
			characterPresences.put(characterTransition.character(), characterPresence);
		}
		return characterPresences;
	}

	private static BinaryDecisionDiagram acceptingStates(VariableOrdering variableOrdering, NFA nfa, VariableSummary variableSummary, SpecifyVariables specifyVariables) {
		List<State> acceptanceStates = nfa.acceptanceStates();
		List<Variable> toStateVariables = variableOrdering.toStateVariables().collect(toList());

		SetVariables firstAcceptanceState = SetVariables.toState(variableSummary, acceptanceStates.get(0));
		BinaryDecisionDiagram acceptingStates = specifyVariables.specifyVariables(toStateVariables, firstAcceptanceState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			State state = acceptanceStates.get(i);
			BinaryDecisionDiagram acceptingState = specifyVariables.specifyVariables(toStateVariables, SetVariables.toState(variableSummary, state));
			acceptingStates = acceptingStates.orTo(acceptingState);
		}
		return acceptingStates;
	}

	private static class SpecifyVariables {
		private final AllVariables allVariables;

		public SpecifyVariables(AllVariables allVariables) {
			this.allVariables = allVariables;
		}

		public BinaryDecisionDiagram specifyVariables(List<Variable> variablesToSpecify, SetVariables setVariables) {
			BinaryDecisionDiagram specifiedVariables = specifyVariable(variablesToSpecify.get(0), setVariables);
			for (int i = 1; i < variablesToSpecify.size(); i++) {
				BinaryDecisionDiagram specifiedVariable = specifyVariable(variablesToSpecify.get(i), setVariables);
				specifiedVariables = specifiedVariables.andTo(specifiedVariable);
			}
			return specifiedVariables;
		}

		private BinaryDecisionDiagram specifyVariable(Variable variable, SetVariables setVariables) {
			if (setVariables.contains(variable)) {
				return allVariables.variable(variable.order());
			} else {
				return allVariables.notVariable(variable.order());
			}
		}
	}

	private static BinaryDecisionDiagram fromState(VariableOrdering variableOrdering, VariableSummary variableSummary, SpecifyVariables specifyVariables, State state) {
		List<Variable> fromStateVariables = variableOrdering.fromStateVariables().collect(toList());
		SetVariables fromState = SetVariables.fromState(variableSummary, state);
		return specifyVariables.specifyVariables(fromStateVariables, fromState);
	}

	private static Permutation relabelToStateToFromState(VariableOrdering variableOrdering, AllVariables allVariables, BinaryDecisionDiagramFactory binaryDecisionDiagramFactory) {
		Stream<BinaryDecisionDiagram> toVariables = variableOrdering.toStateVariablesInOriginalOrder().map(allVariables::variable);
		Stream<BinaryDecisionDiagram> fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().map(allVariables::variable);
		return binaryDecisionDiagramFactory.createPermutation(toVariables, fromVariables);
	}

	private static BinaryDecisionDiagram existsFromStateAndCharacter(VariableOrdering variableOrdering, VariableSummary variableSummary, BinaryDecisionDiagramFactory variableFactory) {
		List<Integer> fromStateOrCharacterVariables = variableOrdering.fromStateOrCharacterVariables().map(Variable::order).collect(toList());
		boolean[] presentVariables = variableSummary.presentVariables(fromStateOrCharacterVariables);
		return variableFactory.newCube(presentVariables);
	}
}

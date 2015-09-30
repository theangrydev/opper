package io.github.theangrydev.opper.scanner.automaton.bfa;

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

		BFAAcceptance bfaAcceptance = BFAAcceptance.bfaAcceptance(nfa, variableSummary, variableOrdering, allVariables);

		BFATransitions bfaTransitions = bfaTransitions(nfa, transitionTable, variableSummary, variableOrdering, binaryDecisionDiagramFactory, allVariables);

		BinaryDecisionDiagram startingFrom = fromState(variableOrdering, variableSummary, allVariables, nfa.initialState());
		Permutation relabelToStateToFromState = relabelToStateToFromState(variableOrdering, allVariables, binaryDecisionDiagramFactory);

		return new BFA(bfaTransitions, bfaAcceptance, startingFrom, relabelToStateToFromState);
	}

	private static BFATransitions bfaTransitions(NFA nfa, TransitionTable transitionTable, VariableSummary variableSummary, VariableOrdering variableOrdering, BinaryDecisionDiagramFactory binaryDecisionDiagramFactory, AllVariables allVariables) {
		BinaryDecisionDiagram transitions = transitions(variableOrdering, allVariables, transitionTable);
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = characterPresence(variableOrdering, nfa.characterTransitions(), variableSummary, allVariables);
		BinaryDecisionDiagram existsFromStateAndCharacter = existsFromStateAndCharacter(variableOrdering, variableSummary, binaryDecisionDiagramFactory);
		return new BFATransitions(transitions, characterPresences, existsFromStateAndCharacter);
	}

	private static BinaryDecisionDiagram transitions(VariableOrdering variableOrders, AllVariables allVariables, TransitionTable transitionTable) {
		List<SetVariables> transitions = transitionTable.transitions();
		BinaryDecisionDiagram allTransitions = allVariables.specifyVariables(variableOrders.allVariables(), transitions.get(0));
		for (int i = 1; i < transitions.size(); i++) {
			BinaryDecisionDiagram transition = allVariables.specifyVariables(variableOrders.allVariables(), transitions.get(i));
			allTransitions = allTransitions.orTo(transition);
		}
		return allTransitions;
	}

	private static Char2ObjectMap<BinaryDecisionDiagram> characterPresence(VariableOrdering variableOrders, List<CharacterTransition> characterTransitions, VariableSummary variableSummary, AllVariables allVariables) {
		List<Variable> characterVariables = variableOrders.characterVariables().collect(toList());
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			SetVariables character = SetVariables.character(variableSummary, characterTransition);
			BinaryDecisionDiagram characterPresence = allVariables.specifyVariables(characterVariables, character);
			characterPresences.put(characterTransition.character(), characterPresence);
		}
		return characterPresences;
	}

	private static BinaryDecisionDiagram fromState(VariableOrdering variableOrdering, VariableSummary variableSummary, AllVariables allVariables, State state) {
		List<Variable> fromStateVariables = variableOrdering.fromStateVariables().collect(toList());
		SetVariables fromState = SetVariables.fromState(variableSummary, state);
		return allVariables.specifyVariables(fromStateVariables, fromState);
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

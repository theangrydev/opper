package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

import java.util.List;

public class BFATransitions {
	private final BinaryDecisionDiagram transitions;
	private final Char2ObjectMap<BinaryDecisionDiagram> characterPresences;
	private final BinaryDecisionDiagram existsFromStateAndCharacter;

	private BFATransitions(BinaryDecisionDiagram transitions, Char2ObjectMap<BinaryDecisionDiagram> characterPresences, BinaryDecisionDiagram existsFromStateAndCharacter) {
		this.transitions = transitions;
		this.characterPresences = characterPresences;
		this.existsFromStateAndCharacter = existsFromStateAndCharacter;
	}

	public static BFATransitions bfaTransitions(NFA nfa, TransitionTable transitionTable, VariableOrdering variableOrdering, AllVariables allVariables) {
		VariableSummary variableSummary = nfa.variableSummary();
		BinaryDecisionDiagram transitions = transitions(allVariables, transitionTable);
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = characterPresence(nfa.characterTransitions(), variableSummary, allVariables);
		BinaryDecisionDiagram existsFromStateAndCharacter = existsFromStateAndCharacter(variableOrdering, variableSummary, allVariables);
		return new BFATransitions(transitions, characterPresences, existsFromStateAndCharacter);
	}

	private static BinaryDecisionDiagram transitions(AllVariables allVariables, TransitionTable transitionTable) {
		List<VariablesSet> specifiedTransitions = transitionTable.transitions();
		BinaryDecisionDiagram allTransitions = allVariables.nothing();
		for (VariablesSet specifiedTransition : specifiedTransitions) {
			BinaryDecisionDiagram transition = allVariables.specifyAllVariables(specifiedTransition);
			allTransitions = allTransitions.orTo(transition);
		}
		return allTransitions;
	}

	private static Char2ObjectMap<BinaryDecisionDiagram> characterPresence(List<CharacterTransition> characterTransitions, VariableSummary variableSummary, AllVariables allVariables) {
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			VariablesSet character = variableSummary.variablesSetForCharacter(characterTransition);
			BinaryDecisionDiagram characterPresence = allVariables.specifyCharacterVariables(character);
			characterPresences.put(characterTransition.character(), characterPresence);
		}
		return characterPresences;
	}

	private static BinaryDecisionDiagram existsFromStateAndCharacter(VariableOrdering variableOrdering, VariableSummary variableSummary, AllVariables allVariables) {
		List<Variable> fromStateOrCharacterVariables = variableOrdering.fromStateOrCharacterVariables();
		boolean[] presentVariables = variableSummary.presentVariables(fromStateOrCharacterVariables);
		return allVariables.newCube(presentVariables);
	}

	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		frontier = frontier.andTo(transitions);
		frontier = frontier.andTo(characterPresences.get(character));
		return frontier.existsTo(existsFromStateAndCharacter);
	}
}

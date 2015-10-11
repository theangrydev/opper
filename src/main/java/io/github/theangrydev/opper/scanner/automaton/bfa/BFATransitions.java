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
	private final BinaryDecisionDiagram nothing;

	private BFATransitions(BinaryDecisionDiagram transitions, Char2ObjectMap<BinaryDecisionDiagram> characterPresences, BinaryDecisionDiagram existsFromStateAndCharacter, BinaryDecisionDiagram nothing) {
		this.transitions = transitions;
		this.characterPresences = characterPresences;
		this.existsFromStateAndCharacter = existsFromStateAndCharacter;
		this.nothing = nothing;
	}

	public static BFATransitions bfaTransitions(NFA nfa, TransitionTable transitionTable, AllVariables allVariables) {
		BinaryDecisionDiagram transitions = transitions(allVariables, transitionTable);
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = characterPresence(nfa.characterTransitions(), allVariables);
		BinaryDecisionDiagram existsFromStateAndCharacter = allVariables.existsFromStateAndCharacter();
		BinaryDecisionDiagram nothing = allVariables.nothing();
		return new BFATransitions(transitions, characterPresences, existsFromStateAndCharacter, nothing);
	}

	private static BinaryDecisionDiagram transitions(AllVariables allVariables, TransitionTable transitionTable) {
		BinaryDecisionDiagram transitions = allVariables.nothing();
		for (VariablesSet transition : transitionTable.transitions()) {
			transitions = transitions.orTo(allVariables.specifyAllVariables(transition));
		}
		return transitions;
	}

	private static Char2ObjectMap<BinaryDecisionDiagram> characterPresence(List<CharacterTransition> characterTransitions, AllVariables allVariables) {
		Char2ObjectMap<BinaryDecisionDiagram> characterPresences = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			characterPresences.put(characterTransition.character(), allVariables.specifyCharacterVariables(characterTransition));
		}
		return characterPresences;
	}

	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		frontier = frontier.andTo(characterPresence(character));
		return frontier.relativeProductTo(transitions, existsFromStateAndCharacter);
	}

	private BinaryDecisionDiagram characterPresence(char character) {
		BinaryDecisionDiagram characterPresence = characterPresences.get(character);
		if (characterPresence != null) {
			return characterPresence;
		}
		return nothing;
	}
}

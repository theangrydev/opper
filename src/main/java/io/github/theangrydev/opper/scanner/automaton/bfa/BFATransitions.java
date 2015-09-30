package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

public class BFATransitions {
	private final BinaryDecisionDiagram transitions;
	private final Char2ObjectMap<BinaryDecisionDiagram> characterPresences;
	private final BinaryDecisionDiagram existsFromStateAndCharacter;

	public BFATransitions(BinaryDecisionDiagram transitions, Char2ObjectMap<BinaryDecisionDiagram> characterPresences, BinaryDecisionDiagram existsFromStateAndCharacter) {
		this.transitions = transitions;
		this.characterPresences = characterPresences;
		this.existsFromStateAndCharacter = existsFromStateAndCharacter;
	}

	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		frontier = frontier.andTo(transitions);
		frontier = frontier.andTo(characterPresences.get(character));
		return frontier.existsTo(existsFromStateAndCharacter);
	}
}

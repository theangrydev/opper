package io.github.theangrydev.opper.scanner.automaton;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

import java.util.ArrayList;
import java.util.List;

public class TransitionFactory {

	private Char2ObjectMap<CharacterTransition> characterTransitionsByCharacter;
	private List<CharacterTransition> transitions;
	private int idSequence;

	public TransitionFactory() {
		transitions = new ArrayList<>();
		characterTransitionsByCharacter = new Char2ObjectArrayMap<>();
	}

	public Transition characterTransition(char character) {
		CharacterTransition transition = characterTransitionsByCharacter.get(character);
		if (transition == null) {
			transition = new CharacterTransition(idSequence++, character);
			transitions.add(transition);
			characterTransitionsByCharacter.put(character, transition);
		}
		return transition;
	}

	public List<CharacterTransition> characterTransitions() {
		return transitions;
	}
}

package io.github.theangrydev.opper.scanner.automaton.nfa;

import io.github.theangrydev.opper.scanner.definition.CharacterClass;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

import java.util.*;

public class TransitionFactory {

	private Char2ObjectMap<CharacterTransition> characterTransitionsByCharacter;
	private Map<CharacterClass, CharacterClassTransition> characterClassTransitionByCharacterClass;
	private int idSequence;

	public TransitionFactory() {
		characterTransitionsByCharacter = new Char2ObjectArrayMap<>();
		characterClassTransitionByCharacterClass = new HashMap<>();
	}

	public Transition characterTransition(char character) {
		CharacterTransition transition = characterTransitionsByCharacter.get(character);
		if (transition == null) {
			transition = new CharacterTransition(idSequence++, character);
			characterTransitionsByCharacter.put(character, transition);
		}
		return transition;
	}

	public Transition characterClassTransition(CharacterClass characterClass) {
		CharacterClassTransition transition = characterClassTransitionByCharacterClass.get(characterClass);
		if (transition == null) {
			transition = new CharacterClassTransition(idSequence++, characterClass);
			characterClassTransitionByCharacterClass.put(characterClass, transition);
		}
		return transition;
	}

	public Collection<CharacterClassTransition> characterClassTransitions() {
		return characterClassTransitionByCharacterClass.values();
	}

	public Collection<CharacterTransition> characterTransitions() {
		return characterTransitionsByCharacter.values();
	}
}

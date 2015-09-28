package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.*;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import jdd.bdd.BDD;
import jdd.bdd.Permutation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.theangrydev.opper.scanner.bdd.BDDRowComputer.bddRow;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class BDDScanner implements Corpus {

	private final char[] charactersToParse;
	private final BDD bdd;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private final int transitionBddTable;
	private final Char2IntMap characterBddSets;
	private final int acceptanceBddSet;
	private final List<State> statesById;
	private final int existsFromStateAndCharacter;
	private final Permutation relabelToStateToFromState;
	private int[] acceptedBuffer;
	private int frontier;
	private Symbol next;
	private int index;

	public BDDScanner(List<SymbolDefinition> symbolDefinitions, char...  charactersToParse) {
		this.charactersToParse = charactersToParse;

		NFA nfa = NFA.convertToNFA(symbolDefinitions);
		nfa.removeEpsilionTransitions();
		nfa.removeUnreachableStates();
		nfa.relabelAccordingToFrequencies();

		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);

		variableSummary = nfa.variableSummary();

		variableOrdering = VariableOrdering.determineOrdering(variableSummary, transitionTable);

		bdd = new BDD(1000,100);
		BDDVariables bddVariables = new BDDVariables(bdd, variableOrdering);

		BDDTransitionsTableComputer bddTransitionsTableComputer = new BDDTransitionsTableComputer();
		transitionBddTable = bddTransitionsTableComputer.compute(variableOrdering, bdd, bddVariables, transitionTable);

		BDDCharacters bddCharacters = new BDDCharacters();
		characterBddSets = bddCharacters.compute(variableOrdering, nfa.characterTransitions(), variableSummary, bdd, bddVariables);

		List<State> states = nfa.states();
		BDDAcceptance bddAcceptance = new BDDAcceptance();
		acceptanceBddSet = bddAcceptance.compute(variableOrdering, states, variableSummary, bdd, bddVariables);

		System.out.println("characterIds=" + nfa.characterTransitions());
		System.out.println("states=" + states.stream().map(Object::toString).collect(Collectors.joining("\n")));
		statesById = concat(Stream.of((State) null), states.stream().sorted(comparing(State::id))).collect(toList());
		System.out.println("characterSets=");
		characterBddSets.char2IntEntrySet().forEach(entry -> {
			System.out.print(entry.getCharKey() + ": ");
			bdd.printSet(entry.getIntValue());
		});

		acceptedBuffer = new int[variableSummary.bitsPerRow()];
		existsFromStateAndCharacter = existsFromStateAndCharacter(variableOrdering, bdd, variableSummary);
		relabelToStateToFromState = relabelToStateToFromState(variableOrdering, bdd, bddVariables);

		frontier = initialFrontier(variableOrdering, bdd, bddVariables, variableSummary, nfa.initialState());
		System.out.println("initial frontier=");
		bdd.printSet(frontier);

	}

	private int lookupToState(VariableOrdering variableOrdering, int[] accepted, VariableSummary variableSummary) {
		int stateIndex = 0;
		for (int i = 0; i < accepted.length; i++) {
			int value = accepted[i];
			if (value == 1) {
				int id = variableOrdering.id(i);
				int bitPosition = variableSummary.unprojectToIdBitPosition(id);
				stateIndex |= (1 << bitPosition);
			}
		}
		return stateIndex;
	}

	private Permutation relabelToStateToFromState(VariableOrdering variableOrdering, BDD bdd, BDDVariables bddVariables) {
		int[] toVariables = variableOrdering.toStateVariablesInOriginalOrder().mapToInt(VariableOrder::order).map(bddVariables::variable).toArray();
		int[] fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().mapToInt(VariableOrder::order).map(bddVariables::variable).toArray();
		return bdd.createPermutation(toVariables, fromVariables);
	}

	private int replaceTo(int nextFrontier, Permutation relabelToStateToFromState, BDD bdd) {
		int result = bdd.replace(nextFrontier, relabelToStateToFromState);
		bdd.deref(nextFrontier);
		return result;
	}

	private int existsTo(int nextFrontier, int existsFromStateAndCharacter, BDD bdd) {
		int result = bdd.ref(bdd.exists(nextFrontier, existsFromStateAndCharacter));
		bdd.deref(nextFrontier);
		return result;
	}

	private int existsFromStateAndCharacter(VariableOrdering variableOrders, BDD bdd, VariableSummary variableSummary) {
		List<Integer> fromStateOrCharacterVariables = variableOrders.fromStateOrCharacters().map(VariableOrder::order).collect(toList());
		boolean[] cube = new boolean[variableSummary.bitsPerRow()];
		for (int present : fromStateOrCharacterVariables) {
			cube[present] = true;
		}
		return bdd.cube(cube);
	}

	private int initialFrontier(VariableOrdering variableOrders, BDD bdd, BDDVariables bddVariables, VariableSummary variableSummary, State initial) {
		List<VariableOrder> fromStateVariableOrders = variableOrders.fromStateVariables().collect(toList());
		SetVariables fromState = SetVariables.fromState(variableSummary, initial);
		return bddRow(fromStateVariableOrders, bdd, bddVariables, fromState);
	}

	@Override
	public Symbol nextSymbol() {
		return next;
	}

	@Override
	public boolean hasNextSymbol() {
		while (index < charactersToParse.length) {
			System.out.println("index=" + index);
			char character = charactersToParse[index++];
			System.out.println("character=" + character);
			frontier = bdd.andTo(frontier, transitionBddTable);
			System.out.println("possible transitions=");
			bdd.printSet(frontier);

			int characterBddSet = characterBddSets.get(character);
			System.out.println("scanned character=");
			bdd.printSet(characterBddSet);

			frontier = bdd.andTo(frontier, characterBddSet);
			System.out.println("possible transitions given scanned character=");
			bdd.printSet(frontier);

			frontier = existsTo(frontier, existsFromStateAndCharacter, bdd);
			System.out.println("possible to states=");
			bdd.printSet(frontier);

			System.out.println("accepted to states=");
			bdd.printSet(acceptanceBddSet);

			int acceptCheck = bdd.ref(bdd.and(acceptanceBddSet, frontier));
			System.out.println("possible acceptance=");
			bdd.printSet(acceptCheck);

			boolean accepted = acceptCheck != bdd.getZero();
			if (accepted) {
				acceptedBuffer = bdd.oneSat(acceptCheck, acceptedBuffer);
				System.out.println("accepted=" + Arrays.toString(acceptedBuffer));
				int stateIndex = lookupToState(variableOrdering, acceptedBuffer, variableSummary);
				State state = statesById.get(stateIndex);
				System.out.println("state=" + state);
				next = state.symbol();
			}
			bdd.deref(acceptCheck);

			frontier = replaceTo(frontier, relabelToStateToFromState, bdd);
			System.out.println("next frontier=");
			bdd.printSet(frontier);

			if (accepted) {
				return true;
			}
		}
		return false;
	}
}

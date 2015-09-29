package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.*;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.theangrydev.opper.scanner.bdd.BDDRowComputer.bddRow;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class BFA {

	private final BDDVariable transitionBddTable;
	private final Char2ObjectMap<BDDVariable> characterBddSets;
	private final BDDVariable acceptanceBddSet;
	private final List<State> statesById;
	private final BDDVariable existsFromStateAndCharacter;
	private final Permutation relabelToStateToFromState;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private int[] acceptedBuffer;
	private BDDVariable frontier;

	public BFA(NFA nfa) {
		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);
		this.variableSummary = nfa.variableSummary();
		this.variableOrdering = VariableOrdering.determineOrdering(variableSummary, transitionTable);

		BDDVariableFactory bddVariableFactory = new BDDVariableFactory();
		BDDVariables bddVariables = new BDDVariables(variableOrdering, bddVariableFactory);

		BDDTransitionsTableComputer bddTransitionsTableComputer = new BDDTransitionsTableComputer();
		transitionBddTable = bddTransitionsTableComputer.compute(variableOrdering, bddVariables, transitionTable);

		BDDCharacters bddCharacters = new BDDCharacters();
		characterBddSets = bddCharacters.compute(variableOrdering, nfa.characterTransitions(), variableSummary, bddVariables);

		List<State> states = nfa.states();
		BDDAcceptance bddAcceptance = new BDDAcceptance();
		acceptanceBddSet = bddAcceptance.compute(variableOrdering, states, variableSummary, bddVariables);

		System.out.println("characterIds=" + nfa.characterTransitions());
		System.out.println("states=" + states.stream().map(Object::toString).collect(Collectors.joining("\n")));
		statesById = concat(Stream.of((State) null), states.stream().sorted(comparing(State::id))).collect(toList());
		System.out.println("characterSets=");
//		characterBddSets.char2IntEntrySet().forEach(entry -> {
//			System.out.print(entry.getCharKey() + ": ");
//			bdd.printSet(entry.getIntValue());
//		});

		acceptedBuffer = new int[variableSummary.bitsPerRow()];
		existsFromStateAndCharacter = existsFromStateAndCharacter(variableOrdering, bddVariableFactory, variableSummary);
		relabelToStateToFromState = relabelToStateToFromState(variableOrdering, bddVariableFactory, bddVariables);

		frontier = initialFrontier(variableOrdering, bddVariables, variableSummary, nfa.initialState());
		System.out.println("initial frontier=");
		frontier.printSet();
	}

	public Optional<Symbol> something(char character) {
		System.out.println("character=" + character);
		frontier = frontier.andTo(transitionBddTable);
		System.out.println("possible transitions=");
		frontier.printSet();

		BDDVariable characterBddSet = characterBddSets.get(character);
		System.out.println("scanned character=");
		characterBddSet.printSet();

		frontier = frontier.andTo(characterBddSet);
		System.out.println("possible transitions given scanned character=");
		frontier.printSet();

		frontier = frontier.existsTo(existsFromStateAndCharacter);
		System.out.println("possible to states=");
		frontier.printSet();

		System.out.println("accepted to states=");
		acceptanceBddSet.printSet();

		BDDVariable acceptCheck = acceptanceBddSet.and(frontier); //bdd.ref(bdd.and(acceptanceBddSet, frontier));
		System.out.println("possible acceptance=");
		acceptCheck.printSet();

		frontier = frontier.replaceTo(relabelToStateToFromState);
		System.out.println("next frontier=");
		frontier.printSet();

		boolean accepted = acceptCheck.isZero();
		if (accepted) {
			acceptedBuffer = acceptCheck.oneSatisfyingAssignment(acceptedBuffer);
			acceptCheck.discard();
			System.out.println("accepted=" + Arrays.toString(acceptedBuffer));
			int stateIndex = lookupToState(variableOrdering, acceptedBuffer, variableSummary);
			State state = statesById.get(stateIndex);
			System.out.println("state=" + state);
			return Optional.of(state.symbol());
		}
		acceptCheck.discard();
		return Optional.empty();
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

	private Permutation relabelToStateToFromState(VariableOrdering variableOrdering, BDDVariableFactory bdd, BDDVariables bddVariables) {
		Stream<BDDVariable> toVariables = variableOrdering.toStateVariablesInOriginalOrder().map(VariableOrder::order).map(bddVariables::variable);
		Stream<BDDVariable> fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().map(VariableOrder::order).map(bddVariables::variable);
		return bdd.createPermutation(toVariables, fromVariables);
	}

	private BDDVariable existsFromStateAndCharacter(VariableOrdering variableOrders, BDDVariableFactory bdd, VariableSummary variableSummary) {
		List<Integer> fromStateOrCharacterVariables = variableOrders.fromStateOrCharacterVariables().map(VariableOrder::order).collect(toList());
		boolean[] setVariables = new boolean[variableSummary.bitsPerRow()];
		for (int present : fromStateOrCharacterVariables) {
			setVariables[present] = true;
		}
		return bdd.newCube(setVariables);
	}

	private BDDVariable initialFrontier(VariableOrdering variableOrders, BDDVariables bddVariables, VariableSummary variableSummary, State initial) {
		List<VariableOrder> fromStateVariableOrders = variableOrders.fromStateVariables().collect(toList());
		SetVariables fromState = SetVariables.fromState(variableSummary, initial);
		return bddRow(fromStateVariableOrders, bddVariables, fromState);
	}
}

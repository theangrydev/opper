package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.*;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.theangrydev.opper.scanner.bdd.BDDRowComputer.bddRow;
import static java.util.stream.Collectors.toList;

public class BFA {

	private final BDDVariable transitionBddTable;
	private final Char2ObjectMap<BDDVariable> characterBddSets;
	private final BDDVariable acceptanceBddSet;
	private final List<State> statesById;
	private final BDDVariable existsFromStateAndCharacter;
	private final Permutation relabelToStateToFromState;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private BDDVariable frontier;

	public BFA(NFA nfa) {
		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);
		this.variableSummary = nfa.variableSummary();
		this.variableOrdering = VariableOrdering.determineOrdering(variableSummary, transitionTable);

		BDDVariableFactory bddVariableFactory = new BDDVariableFactory();
		BDDVariables bddVariables = new BDDVariables(variableOrdering, bddVariableFactory);

		BDDTransitionsTableComputer bddTransitionsTableComputer = new BDDTransitionsTableComputer();
		transitionBddTable = bddTransitionsTableComputer.compute(variableOrdering, bddVariables, transitionTable);

		characterBddSets = computeCharacterBddSets(variableOrdering, nfa.characterTransitions(), variableSummary, bddVariables);

		acceptanceBddSet = computeAcceptanceSet(variableOrdering, nfa, variableSummary, bddVariables);

		System.out.println("characterIds=" + nfa.characterTransitions());
		System.out.println("states=" + nfa.states().stream().map(Object::toString).collect(Collectors.joining("\n")));
		statesById = nfa.statesById();
		System.out.println("characterSets=");
		characterBddSets.entrySet().forEach(entry -> {
			System.out.print(entry.getKey() + ": ");
			entry.getValue().printSet();
		});

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
			int[] assignment = acceptCheck.oneSatisfyingAssignment();
			acceptCheck.discard();
			System.out.println("accepted=" + Arrays.toString(assignment));
			int stateIndex = lookupToState(variableOrdering, assignment, variableSummary);
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

	private Char2ObjectMap<BDDVariable> computeCharacterBddSets(VariableOrdering variableOrders, List<CharacterTransition> characterTransitions, VariableSummary variableSummary, BDDVariables bddVariables) {
		List<VariableOrder> characterVariableOrders = variableOrders.characterVariables().collect(toList());
		Char2ObjectMap<BDDVariable> characterBddSets = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			SetVariables character = SetVariables.character(variableSummary, characterTransition);
			BDDVariable bddRow = BDDRowComputer.bddRow(characterVariableOrders, bddVariables, character);
			characterBddSets.put(characterTransition.character(), bddRow);
		}
		return characterBddSets;
	}

	private BDDVariable computeAcceptanceSet(VariableOrdering variableOrdering, NFA nfa, VariableSummary variableSummary, BDDVariables bddVariables) {
		List<State> acceptanceStates = nfa.acceptanceStates();
		List<VariableOrder> toStateVariableOrders = variableOrdering.toStateVariables().collect(toList());

		SetVariables firstToState = SetVariables.toState(variableSummary, acceptanceStates.get(0));
		BDDVariable bddDisjunction = BDDRowComputer.bddRow(toStateVariableOrders, bddVariables, firstToState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			State state = acceptanceStates.get(i);
			SetVariables toState = SetVariables.toState(variableSummary, state);
			BDDVariable bddRow = BDDRowComputer.bddRow(toStateVariableOrders, bddVariables, toState);
			bddDisjunction = bddDisjunction.orTo(bddRow);
		}
		return bddDisjunction;
	}
}

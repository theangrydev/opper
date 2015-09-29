package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.*;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BFA {

	private final BDDVariable transitionBddTable;
	private final Char2ObjectMap<BDDVariable> characterBddSets;
	private final BDDVariable acceptanceBddSet;
	private final List<Symbol> symbolsByStateId;
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

		transitionBddTable = computeTransitionTable(variableOrdering, bddVariables, transitionTable);
		characterBddSets = computeCharacterBddSets(variableOrdering, nfa.characterTransitions(), variableSummary, bddVariables);
		acceptanceBddSet = computeAcceptanceSet(variableOrdering, nfa, variableSummary, bddVariables);
		existsFromStateAndCharacter = existsFromStateAndCharacter(variableOrdering, bddVariableFactory, variableSummary);
		relabelToStateToFromState = relabelToStateToFromState(variableOrdering, bddVariableFactory, bddVariables);
		frontier = initialFrontier(variableOrdering, bddVariables, variableSummary, nfa.initialState());
		symbolsByStateId = nfa.symbolsByStateId();

		System.out.println("characterIds=" + nfa.characterTransitions());
		System.out.println("states=" + nfa.states().stream().map(Object::toString).collect(Collectors.joining("\n")));
		System.out.println("characterSets=");
		characterBddSets.entrySet().forEach(entry -> {
			System.out.print(entry.getKey() + ": ");
			entry.getValue().printSet();
		});

		System.out.println("initial frontier=");
		frontier.printSet();
	}

	private BDDVariable computeTransitionTable(VariableOrdering variableOrders, BDDVariables bddVariables, TransitionTable transitionTable) {
		List<SetVariables> transitions = transitionTable.transitions();
		BDDVariable bddDisjunction = BFA.bddRow(variableOrders.allVariables(), bddVariables, transitions.get(0));
		for (int i = 1; i < transitions.size(); i++) {
			BDDVariable bddRow = BFA.bddRow(variableOrders.allVariables(), bddVariables, transitions.get(i));
			bddDisjunction = bddDisjunction.orTo(bddRow);
		}
		return bddDisjunction;
	}

	private static BDDVariable bddRow(List<VariableOrder> variableOrders, BDDVariables bddVariables, SetVariables setVariables) {
		BDDVariable bddRow = setVariable(bddVariables, setVariables, variableOrders.get(0));
		for (int i = 1; i < variableOrders.size(); i++) {
			BDDVariable bddVariable = setVariable(bddVariables, setVariables, variableOrders.get(i));
			bddRow = bddRow.andTo(bddVariable);
		}
		return bddRow;
	}

	private static BDDVariable setVariable(BDDVariables bddVariables, SetVariables setVariables, VariableOrder variableOrder) {
		if (setVariables.contains(variableOrder)) {
			return bddVariables.variable(variableOrder.order());
		} else {
			return bddVariables.notVariable(variableOrder.order());
		}
	}

	public Optional<Symbol> scan(char character) {
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

		BDDVariable acceptCheck = acceptanceBddSet.and(frontier);
		System.out.println("possible acceptance=");
		acceptCheck.printSet();

		frontier = frontier.replaceTo(relabelToStateToFromState);
		System.out.println("next frontier=");
		frontier.printSet();

		boolean accepted = acceptCheck.isZero();
		if (accepted) {
			BDDVariableAssignment assignment = acceptCheck.oneSatisfyingAssignment();
			acceptCheck.discard();
			System.out.println("accepted=" + assignment);
			int stateIndex = lookupToState(variableOrdering, assignment, variableSummary);
			Symbol acceptedSymbol = symbolsByStateId.get(stateIndex);
			return Optional.of(acceptedSymbol);
		}
		acceptCheck.discard();
		return Optional.empty();
	}

	private int lookupToState(VariableOrdering variableOrdering, BDDVariableAssignment accepted, VariableSummary variableSummary) {
		return accepted.assignedIndexes().map(variableOrdering::id).map(variableSummary::unprojectToIdBitPosition).map(bitPosition -> 1 << bitPosition).reduce(0, (a, b) -> a | b);
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
			BDDVariable bddRow = bddRow(characterVariableOrders, bddVariables, character);
			characterBddSets.put(characterTransition.character(), bddRow);
		}
		return characterBddSets;
	}

	private BDDVariable computeAcceptanceSet(VariableOrdering variableOrdering, NFA nfa, VariableSummary variableSummary, BDDVariables bddVariables) {
		List<State> acceptanceStates = nfa.acceptanceStates();
		List<VariableOrder> toStateVariableOrders = variableOrdering.toStateVariables().collect(toList());

		SetVariables firstToState = SetVariables.toState(variableSummary, acceptanceStates.get(0));
		BDDVariable bddDisjunction = bddRow(toStateVariableOrders, bddVariables, firstToState);
		for (int i = 1; i < acceptanceStates.size(); i++) {
			State state = acceptanceStates.get(i);
			SetVariables toState = SetVariables.toState(variableSummary, state);
			BDDVariable bddRow = bddRow(toStateVariableOrders, bddVariables, toState);
			bddDisjunction = bddDisjunction.orTo(bddRow);
		}
		return bddDisjunction;
	}
}

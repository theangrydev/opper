package io.github.theangrydev.opper.scanner.bdd;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.*;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BFA {

	private final BDDVariable transitionBddTable;
	private final Char2ObjectMap<BDDVariable> characterBddSets;
	private final BDDVariable acceptanceBddSet;
	private final BDDVariable startingFrom;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private final BDDVariableFactory bddVariableFactory;
	private final BDDVariables bddVariables;
	private final List<Symbol> symbolsByStateId;

	public BFA(NFA nfa) {
		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);
		this.variableSummary = nfa.variableSummary();
		this.variableOrdering = VariableOrdering.determineOrdering(variableSummary, transitionTable);

		bddVariableFactory = new BDDVariableFactory();
		bddVariables = new BDDVariables(variableOrdering, bddVariableFactory);

		symbolsByStateId = nfa.symbolsByStateId();
		startingFrom = fromState(nfa.initialState());
		transitionBddTable = computeTransitionTable(variableOrdering, bddVariables, transitionTable);
		characterBddSets = computeCharacterBddSets(variableOrdering, nfa.characterTransitions(), variableSummary, bddVariables);
		acceptanceBddSet = computeAcceptanceSet(variableOrdering, nfa, variableSummary, bddVariables);
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

	public static BDDVariable bddRow(List<VariableOrder> variableOrders, BDDVariables bddVariables, SetVariables setVariables) {
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

	public BDDVariable transitionBddTable() {
		return transitionBddTable;
	}

	public BDDVariable characterBddSet(char character) {
		return characterBddSets.get(character);
	}

	public BDDVariable acceptanceBddSet() {
		return acceptanceBddSet;
	}


	public int lookupToState(BDDVariableAssignment accepted) {
		return accepted.assignedIndexes().map(variableOrdering::id).map(variableSummary::unprojectToIdBitPosition).map(bitPosition -> 1 << bitPosition).reduce(0, (a, b) -> a | b);
	}

	public BDDVariable existsFromStateAndCharacter() {
		List<Integer> fromStateOrCharacterVariables = variableOrdering.fromStateOrCharacterVariables().map(VariableOrder::order).collect(toList());
		boolean[] setVariables = new boolean[variableSummary.bitsPerRow()];
		for (int present : fromStateOrCharacterVariables) {
			setVariables[present] = true;
		}
		return bddVariableFactory.newCube(setVariables);
	}

	public Permutation relabelToStateToFromState() {
		Stream<BDDVariable> toVariables = variableOrdering.toStateVariablesInOriginalOrder().map(VariableOrder::order).map(bddVariables::variable);
		Stream<BDDVariable> fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().map(VariableOrder::order).map(bddVariables::variable);
		return bddVariableFactory.createPermutation(toVariables, fromVariables);
	}

	private BDDVariable fromState(State state) {
		List<VariableOrder> fromStateVariableOrders = variableOrdering.fromStateVariables().collect(toList());
		SetVariables fromState = SetVariables.fromState(variableSummary, state);
		return BFA.bddRow(fromStateVariableOrders, bddVariables, fromState);
	}

	public BDDVariable startState() {
		return startingFrom;
	}

	public Symbol symbolForStateIndex(int stateIndex) {
		return symbolsByStateId.get(stateIndex);
	}
}

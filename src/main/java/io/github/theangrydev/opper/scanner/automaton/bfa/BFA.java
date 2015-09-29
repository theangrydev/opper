package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.automaton.nfa.CharacterTransition;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.State;
import io.github.theangrydev.opper.scanner.bdd.BDDVariable;
import io.github.theangrydev.opper.scanner.bdd.BDDVariableAssignment;
import io.github.theangrydev.opper.scanner.bdd.BDDVariableFactory;
import io.github.theangrydev.opper.scanner.bdd.BDDVariables;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BFA {

	private final BDDVariable transitionBddTable;
	private final Char2ObjectMap<BDDVariable> characterBddSets;
	private final BDDVariable acceptanceBddSet;
	private final BDDVariable initialState;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private final List<Symbol> symbolsByStateId;
	private final Permutation relabelToStateToFromState;
	private final BDDVariable existsFromStateAndCharacter;

	public static BFA convertToBFA(NFA nfa) {
		TransitionTable transitionTable = TransitionTable.fromNFA(nfa);
		VariableSummary variableSummary = nfa.variableSummary();
		VariableOrdering variableOrdering = VariableOrdering.determineOrdering(variableSummary, transitionTable);

		BDDVariableFactory bddVariableFactory = new BDDVariableFactory();
		BDDVariables bddVariables = new BDDVariables(variableOrdering.numberOfVariables(), bddVariableFactory);
		List<Symbol> symbolsByStateId = nfa.symbolsByStateId();
		BDDVariable startingFrom = fromState(variableOrdering, variableSummary, bddVariables, nfa.initialState());
		BDDVariable transitionBddTable = computeTransitionTable(variableOrdering, bddVariables, transitionTable);
		Char2ObjectMap<BDDVariable>  characterBddSets = computeCharacterBddSets(variableOrdering, nfa.characterTransitions(), variableSummary, bddVariables);
		BDDVariable acceptanceBddSet = computeAcceptanceSet(variableOrdering, nfa, variableSummary, bddVariables);
		Permutation relabelToStateToFromState = relabelToStateToFromState(variableOrdering, bddVariables, bddVariableFactory);
		BDDVariable existsFromStateAndCharacter = existsFromStateAndCharacter(variableOrdering, variableSummary, bddVariableFactory);
		return new BFA(transitionBddTable, characterBddSets, acceptanceBddSet, startingFrom, variableOrdering, variableSummary, symbolsByStateId, relabelToStateToFromState, existsFromStateAndCharacter);
	}

	private BFA(BDDVariable transitionBddTable, Char2ObjectMap<BDDVariable> characterBddSets, BDDVariable acceptanceBddSet, BDDVariable initialState, VariableOrdering variableOrdering, VariableSummary variableSummary, List<Symbol> symbolsByStateId, Permutation relabelToStateToFromState, BDDVariable existsFromStateAndCharacter) {
		this.transitionBddTable = transitionBddTable;
		this.characterBddSets = characterBddSets;
		this.acceptanceBddSet = acceptanceBddSet;
		this.initialState = initialState;
		this.variableOrdering = variableOrdering;
		this.variableSummary = variableSummary;
		this.symbolsByStateId = symbolsByStateId;
		this.relabelToStateToFromState = relabelToStateToFromState;
		this.existsFromStateAndCharacter = existsFromStateAndCharacter;
	}

	private static BDDVariable computeTransitionTable(VariableOrdering variableOrders, BDDVariables bddVariables, TransitionTable transitionTable) {
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

	private static Char2ObjectMap<BDDVariable> computeCharacterBddSets(VariableOrdering variableOrders, List<CharacterTransition> characterTransitions, VariableSummary variableSummary, BDDVariables bddVariables) {
		List<VariableOrder> characterVariableOrders = variableOrders.characterVariables().collect(toList());
		Char2ObjectMap<BDDVariable> characterBddSets = new Char2ObjectArrayMap<>(characterTransitions.size());
		for (CharacterTransition characterTransition : characterTransitions) {
			SetVariables character = SetVariables.character(variableSummary, characterTransition);
			BDDVariable bddRow = bddRow(characterVariableOrders, bddVariables, character);
			characterBddSets.put(characterTransition.character(), bddRow);
		}
		return characterBddSets;
	}

	private static BDDVariable computeAcceptanceSet(VariableOrdering variableOrdering, NFA nfa, VariableSummary variableSummary, BDDVariables bddVariables) {
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

	private static BDDVariable fromState(VariableOrdering variableOrdering, VariableSummary variableSummary, BDDVariables bddVariables, State state) {
		List<VariableOrder> fromStateVariableOrders = variableOrdering.fromStateVariables().collect(toList());
		SetVariables fromState = SetVariables.fromState(variableSummary, state);
		return BFA.bddRow(fromStateVariableOrders, bddVariables, fromState);
	}

	private static Permutation relabelToStateToFromState(VariableOrdering variableOrdering, BDDVariables bddVariables, BDDVariableFactory bddVariableFactory) {
		Stream<BDDVariable> toVariables = variableOrdering.toStateVariablesInOriginalOrder().map(VariableOrder::order).map(bddVariables::variable);
		Stream<BDDVariable> fromVariables = variableOrdering.fromStateVariablesInOriginalOrder().map(VariableOrder::order).map(bddVariables::variable);
		return bddVariableFactory.createPermutation(toVariables, fromVariables);
	}

	private static BDDVariable existsFromStateAndCharacter(VariableOrdering variableOrdering, VariableSummary variableSummary, BDDVariableFactory variableFactory) {
		List<Integer> fromStateOrCharacterVariables = variableOrdering.fromStateOrCharacterVariables().map(VariableOrder::order).collect(toList());
		boolean[] setVariables = new boolean[variableSummary.bitsPerRow()];
		for (int present : fromStateOrCharacterVariables) {
			setVariables[present] = true;
		}
		return variableFactory.newCube(setVariables);
	}

	public BDDVariable transitionBddTable() {
		return transitionBddTable;
	}

	public BDDVariable characterBddSet(char character) {
		return characterBddSets.get(character);
	}

	public BDDVariable relabelToStateToFromState(BDDVariable frontier) {
		return frontier.replaceTo(relabelToStateToFromState);
	}

	public BDDVariable initialState() {
		return initialState;
	}

	public Symbol symbolForAssignment(BDDVariableAssignment assignment) {
		return symbolsByStateId.get(lookupToState(assignment));
	}

	private int lookupToState(BDDVariableAssignment assignment) {
		return assignment.assignedIndexes()
			.map(variableOrdering::id)
			.map(variableSummary::unprojectToIdBitPosition)
			.map(bitPosition -> 1 << bitPosition)
			.reduce(0, (a, b) -> a | b);
	}

	public Optional<Symbol> checkAcceptance(BDDVariable frontier) {
		BDDVariable acceptCheck = acceptanceBddSet.and(frontier);
		boolean accepted = acceptCheck.isNotZero();
		if (!accepted) {
			acceptCheck.discard();
			return Optional.empty();
		}
		acceptCheck.discard();
		BDDVariableAssignment satisfyingAssignment = acceptCheck.oneSatisfyingAssignment();
		Symbol acceptedSymbol = symbolForAssignment(satisfyingAssignment);
		return Optional.of(acceptedSymbol);
	}

	public BDDVariable transition(BDDVariable frontier, char character) {
		frontier = frontier.andTo(transitionBddTable());
		frontier = frontier.andTo(characterBddSet(character));
		return frontier.existsTo(existsFromStateAndCharacter);
	}
}

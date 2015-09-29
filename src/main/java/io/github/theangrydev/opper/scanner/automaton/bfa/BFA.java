package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.bdd.BDDVariable;
import io.github.theangrydev.opper.scanner.bdd.BDDVariableAssignment;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.Optional;

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

	public BFA(BDDVariable transitionBddTable, Char2ObjectMap<BDDVariable> characterBddSets, BDDVariable acceptanceBddSet, BDDVariable initialState, VariableOrdering variableOrdering, VariableSummary variableSummary, List<Symbol> symbolsByStateId, Permutation relabelToStateToFromState, BDDVariable existsFromStateAndCharacter) {
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

	public BDDVariable relabelToStateToFromState(BDDVariable frontier) {
		return frontier.replaceTo(relabelToStateToFromState);
	}

	public BDDVariable initialState() {
		return initialState;
	}

	private Symbol symbolForAssignment(BDDVariableAssignment assignment) {
		return symbolsByStateId.get(lookupToState(assignment));
	}

	private int lookupToState(BDDVariableAssignment assignment) {
		return assignment.assignedIndexes()
			.map(variableOrdering::variableId)
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
		frontier = frontier.andTo(transitionBddTable);
		frontier = frontier.andTo(characterBddSets.get(character));
		return frontier.existsTo(existsFromStateAndCharacter);
	}
}

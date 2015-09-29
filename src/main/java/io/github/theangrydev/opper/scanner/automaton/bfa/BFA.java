package io.github.theangrydev.opper.scanner.automaton.bfa;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagram;
import io.github.theangrydev.opper.scanner.bdd.BinaryDecisionDiagramVariableAssignment;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import jdd.bdd.Permutation;

import java.util.List;
import java.util.Optional;

public class BFA {

	private final BinaryDecisionDiagram transitions;
	private final Char2ObjectMap<BinaryDecisionDiagram> characterPresences;
	private final BinaryDecisionDiagram acceptanceingStates;
	private final BinaryDecisionDiagram initialState;
	private final VariableOrdering variableOrdering;
	private final VariableSummary variableSummary;
	private final List<Symbol> symbolsByStateId;
	private final Permutation relabelToStateToFromState;
	private final BinaryDecisionDiagram existsFromStateAndCharacter;

	public BFA(BinaryDecisionDiagram transitions, Char2ObjectMap<BinaryDecisionDiagram> characterPresences, BinaryDecisionDiagram acceptanceingStates, BinaryDecisionDiagram initialState, VariableOrdering variableOrdering, VariableSummary variableSummary, List<Symbol> symbolsByStateId, Permutation relabelToStateToFromState, BinaryDecisionDiagram existsFromStateAndCharacter) {
		this.transitions = transitions;
		this.characterPresences = characterPresences;
		this.acceptanceingStates = acceptanceingStates;
		this.initialState = initialState;
		this.variableOrdering = variableOrdering;
		this.variableSummary = variableSummary;
		this.symbolsByStateId = symbolsByStateId;
		this.relabelToStateToFromState = relabelToStateToFromState;
		this.existsFromStateAndCharacter = existsFromStateAndCharacter;
	}

	public BinaryDecisionDiagram relabelToStateToFromState(BinaryDecisionDiagram frontier) {
		return frontier.replaceTo(relabelToStateToFromState);
	}

	public BinaryDecisionDiagram initialState() {
		return initialState;
	}

	private Symbol symbolForAssignment(BinaryDecisionDiagramVariableAssignment assignment) {
		return symbolsByStateId.get(lookupToState(assignment));
	}

	private int lookupToState(BinaryDecisionDiagramVariableAssignment assignment) {
		return assignment.assignedVariableIndexes()
			.map(variableOrdering::variableId)
			.map(variableSummary::unprojectToIdBitPosition)
			.map(bitPosition -> 1 << bitPosition)
			.reduce(0, (a, b) -> a | b);
	}

	public Optional<Symbol> checkAcceptance(BinaryDecisionDiagram frontier) {
		BinaryDecisionDiagram acceptCheck = acceptanceingStates.and(frontier);
		boolean accepted = acceptCheck.isNotZero();
		if (!accepted) {
			acceptCheck.discard();
			return Optional.empty();
		}
		acceptCheck.discard();
		BinaryDecisionDiagramVariableAssignment satisfyingAssignment = acceptCheck.oneSatisfyingAssignment();
		Symbol acceptedSymbol = symbolForAssignment(satisfyingAssignment);
		return Optional.of(acceptedSymbol);
	}

	public BinaryDecisionDiagram transition(BinaryDecisionDiagram frontier, char character) {
		frontier = frontier.andTo(transitions);
		frontier = frontier.andTo(characterPresences.get(character));
		return frontier.existsTo(existsFromStateAndCharacter);
	}
}

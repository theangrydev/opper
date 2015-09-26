package io.github.theangrydev.opper.scanner.autonoma;

import io.github.theangrydev.opper.corpus.Corpus;
import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import jdd.bdd.BDD;
import jdd.bdd.Permutation;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.theangrydev.opper.scanner.autonoma.BDDRowComputer.bddRow;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class BDDScanner implements Corpus {

	private final char[] charactersToParse;
	private final BDD bdd;
	private final List<Variable> variables;
	private final BitSummary bitSummary;
	private final int transitionBddTable;
	private final Char2IntMap characterBddSets;
	private final int acceptanceBddSet;
	private int frontier;
	private final List<State> statesById;
	private final int existsFromStateAndCharacter;
	private final Permutation relabelToStateToFromState;
	private int[] accepted;
	private Symbol next;
	private int index;

	public BDDScanner(List<SymbolDefinition> symbolDefinitions, char...  charactersToParse) {
		this.charactersToParse = charactersToParse;
		StateFactory stateFactory = new StateFactory();
		SymbolDefinitionToStateConverter converter = new SymbolDefinitionToStateConverter(stateFactory);

		State initial = converter.convertDefinitionsToStates(symbolDefinitions);

		stateFactory.eliminateEpsilonTransitions();
		initial.markReachableStates();
		stateFactory.removeUnreachableStates();

		StateStatistics stateStatistics = new StateStatistics();
		stateFactory.states().forEach(stateStatistics::record);

		CharacterEncoder characterEncoder = new CharacterEncoder();
		Char2IntMap characterIds = characterEncoder.labelCharactersWithSmallerIdsForMoreFrequentCharacters(stateStatistics.characterFrequencies());

		StateEncoder stateEncoder = new StateEncoder();
		stateEncoder.labelStatesWithSmallerIdsForMoreFrequentStates(stateStatistics.stateFrequencies());

		bitSummary = new BitSummary(stateFactory.states().size(), characterIds.size());

		TransitionTableBuilder transitionTableBuilder = new TransitionTableBuilder();
		List<BitSet> transitionTable = transitionTableBuilder.buildTransitionTable(bitSummary, characterIds, stateFactory.states());

		VariableOrderingCalculator variableOrderingCalculator = new VariableOrderingCalculator();
		variables = variableOrderingCalculator.determineOrdering(bitSummary.bitsPerRow(), transitionTable);

		bdd = new BDD(1000,100);
		BDDVariables bddVariables = new BDDVariables(bdd, variables);

		BDDTransitionsTableComputer bddTransitionsTableComputer = new BDDTransitionsTableComputer();
		transitionBddTable = bddTransitionsTableComputer.compute(variables, bdd, bddVariables, transitionTable);

		BDDCharacters bddCharacters = new BDDCharacters();
		characterBddSets = bddCharacters.compute(variables, characterIds, bitSummary, bdd, bddVariables);

		BDDAcceptance bddAcceptance = new BDDAcceptance();
		acceptanceBddSet = bddAcceptance.compute(variables, stateFactory.states(), bitSummary, bdd, bddVariables);

		System.out.println("characterIds="+characterIds);
		System.out.println("states=" + stateFactory.states().stream().map(Object::toString).collect(Collectors.joining("\n")));
		statesById = concat(Stream.of((State) null), stateFactory.states().stream().sorted(comparing(State::id))).collect(toList());

		accepted = new int[bitSummary.bitsPerRow()];
		existsFromStateAndCharacter = existsFromStateAndCharacter(variables, bdd, bitSummary);
		relabelToStateToFromState = relabelToStateToFromState(variables, bdd, bddVariables, bitSummary);

		frontier = initialFrontier(variables, bdd, bddVariables, bitSummary, initial);
		System.out.println("initial frontier=");
		bdd.printSet(frontier);

	}

	private int lookupToState(List<Variable> variables, int[] accepted, BitSummary bitSummary) {
		int stateIndex = 0;
		for (int i = 0; i < accepted.length; i++) {
			int value = accepted[i];
			if (value == 1) {
				int id = variables.get(i).id();
				int bitPosition = bitSummary.unprojectToIdBitPosition(id);
				stateIndex |= (1 << bitPosition);
			}
		}
		return stateIndex;
	}

	private Permutation relabelToStateToFromState(List<Variable> variables, BDD bdd, BDDVariables bddVariables, BitSummary bitSummary) {
		int[] toVariables = variables.stream().filter(bitSummary::isToState).sorted(comparing(Variable::id)).mapToInt(Variable::order).map(bddVariables::variable).toArray();
		int[] fromVariables = variables.stream().filter(bitSummary::isFromState).sorted(comparing(Variable::id)).mapToInt(Variable::order).map(bddVariables::variable).toArray();
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

	private int existsFromStateAndCharacter(List<Variable> variables, BDD bdd, BitSummary bitSummary) {
		List<Integer> fromStateOrCharacterVariables = variables.stream().filter(bitSummary::isFromStateOrCharacter).map(Variable::order).collect(toList());
		boolean[] cube = new boolean[bitSummary.bitsPerRow()];
		for (int present : fromStateOrCharacterVariables) {
			cube[present] = true;
		}
		return bdd.cube(cube);
	}

	private int initialFrontier(List<Variable> variables, BDD bdd, BDDVariables bddVariables, BitSummary bitSummary, State initial) {
		List<Variable> fromStateVariables = variables.stream().filter(bitSummary::isFromState).collect(toList());
		BitSet fromState = BitSet.valueOf(new long[]{bitSummary.projectFromId(initial.id())});
		return bddRow(fromStateVariables, bdd, bddVariables, fromState);
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
			System.out.println("transitionBddTable=");
			bdd.printSet(frontier);

			int characterBddSet = characterBddSets.get(character);
			System.out.println("characterBddSet=");
			bdd.printSet(characterBddSet);

			frontier = bdd.andTo(frontier, characterBddSet);
			System.out.println("character=");
			bdd.printSet(frontier);

			frontier = existsTo(frontier, existsFromStateAndCharacter, bdd);
			System.out.println("existsFromStateAndCharacter=");
			bdd.printSet(frontier);

			int acceptCheck = bdd.ref(bdd.and(acceptanceBddSet, frontier));
			System.out.println("acceptCheckBddSet=");
			bdd.printSet(acceptCheck);

			if (acceptCheck != bdd.getZero()) {
				accepted = bdd.oneSat(acceptCheck, accepted);
				System.out.println("accepted=" + Arrays.toString(accepted));
				int stateIndex = lookupToState(variables, accepted, bitSummary);
				State state = statesById.get(stateIndex);
				System.out.println("state=" + state);
				next = state.symbol();
				return true;
			}
			bdd.deref(acceptCheck);

			frontier = replaceTo(frontier, relabelToStateToFromState, bdd);
			System.out.println("relabelToStateToFromState=");
			bdd.printSet(frontier);

			System.out.println("frontier=");
			bdd.printSet(frontier);
		}
		return false;
	}
}

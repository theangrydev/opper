package io.github.theangrydev.opper.scanner.definition;

import io.github.theangrydev.opper.grammar.Symbol;
import io.github.theangrydev.opper.scanner.autonoma.*;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import jdd.bdd.BDD;
import org.junit.Test;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.theangrydev.opper.scanner.definition.AlternativeExpression.either;
import static io.github.theangrydev.opper.scanner.definition.CharacterExpression.character;
import static io.github.theangrydev.opper.scanner.definition.ConcatenateExpression.concatenate;
import static io.github.theangrydev.opper.scanner.definition.RepeatExpression.repeat;

public class StatesCanBeConstructedUsingExpressionsTest {

	@Test
	public void shouldConstructStatesFromAnExpression() {
		StateFactory stateFactory = new StateFactory();
		SymbolDefinitionToStateConverter converter = new SymbolDefinitionToStateConverter(stateFactory);

		Expression expression = repeat(either(character('a'), character('b'), concatenate(character('c'), character('d'))));
		SymbolDefinition symbolDefinition = new SymbolDefinition(new Symbol(1, "symbol"), expression);

		State initial = converter.convertDefinitionsToStates(Collections.singletonList(symbolDefinition));

		stateFactory.eliminateEpsilonTransitions();
		initial.markReachableStates();
		stateFactory.removeUnreachableStates();

		StateStatistics stateStatistics = new StateStatistics();
		stateFactory.states().forEach(stateStatistics::record);

		CharacterEncoder characterEncoder = new CharacterEncoder();
		Char2IntMap characterIds = characterEncoder.labelCharactersWithSmallerIdsForMoreFrequentCharacters(stateStatistics.characterFrequencies());

		StateEncoder stateEncoder = new StateEncoder();
		stateEncoder.labelStatesWithSmallerIdsForMoreFrequentStates(stateStatistics.stateFrequencies());

		BitSummary bitSummary = new BitSummary(stateFactory.states().size(), characterIds.size());

		TransitionTableBuilder transitionTableBuilder = new TransitionTableBuilder();
		List<BitSet> transitionTable = transitionTableBuilder.buildTransitionTable(bitSummary, characterIds, stateFactory.states());

		VariableOrderingCalculator variableOrderingCalculator = new VariableOrderingCalculator();
		List<Variable> variables = variableOrderingCalculator.determineOrdering(bitSummary.bitsPerRow(), transitionTable);

		BDD bdd = new BDD(1000,100);
		BDDVariables bddVariables = new BDDVariables(bdd, variables);

		BDDTransitionsTable bddTransitionsTable = new BDDTransitionsTable();
		bddTransitionsTable.compute(variables, bdd, bddVariables, transitionTable);

		BDDCharacters bddCharacters = new BDDCharacters();
		bddCharacters.compute(variables, characterIds, bitSummary, bdd, bddVariables);

		BDDAcceptance bddAcceptance = new BDDAcceptance();
		bddAcceptance.compute(variables, stateFactory.states(), bitSummary, bdd, bddVariables);

		System.out.println(stateFactory.states().stream().map(Object::toString).collect(Collectors.joining("\n")));
		System.out.println("bitsummary=" + bitSummary);
		System.out.println("characters=" + characterIds);
		System.out.println("transitions=\n" +transitionTable.stream().map(Object::toString).collect(Collectors.joining("\n")));
		System.out.println("ordering=" + variables);
		System.out.println(stateStatistics);
	}
}

package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.scanner.automaton.bfa.BFA;
import io.github.theangrydev.opper.scanner.automaton.bfa.BFABuilder;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFABuilder;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.io.Reader;
import java.util.List;

public class BFAScannerFactory implements ScannerFactory {

	private final BFA bfa;

	public BFAScannerFactory(List<SymbolDefinition> symbolDefinitions) {
		NFA nfa = NFABuilder.convertToNFA(symbolDefinitions);
		nfa.removeEpsilionTransitions();
		nfa.removeUnreachableStates();
		nfa.relabelAccordingToFrequencies();
		bfa = BFABuilder.convertToBFA(nfa);
	}

	@Override
	public Scanner scanner(Reader charactersToParse) {
		return new BFAScanner(bfa, charactersToParse);
	}
}

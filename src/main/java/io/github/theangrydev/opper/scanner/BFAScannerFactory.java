/*
 * Copyright 2015-2020 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.theangrydev.opper.scanner;

import io.github.theangrydev.opper.scanner.automaton.bfa.BFA;
import io.github.theangrydev.opper.scanner.automaton.bfa.BFABuilder;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFA;
import io.github.theangrydev.opper.scanner.automaton.nfa.NFABuilder;
import io.github.theangrydev.opper.scanner.definition.SymbolDefinition;

import java.io.IOException;
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
    public Scanner scanner(Reader charactersToParse) throws IOException {
        return BFAScanner.createBFAScanner(bfa, charactersToParse);
    }
}

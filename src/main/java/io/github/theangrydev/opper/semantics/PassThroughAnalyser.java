package io.github.theangrydev.opper.semantics;

import io.github.theangrydev.opper.semantics.ParseTreeLeafAnalyser.LeafAnalyser;

public class PassThroughAnalyser implements LeafAnalyser<String> {
	@Override
	public String analyse(String content) {
		return content;
	}
}

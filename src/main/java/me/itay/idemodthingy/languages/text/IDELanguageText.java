package me.itay.idemodthingy.languages.text;

import java.awt.Color;

import me.itay.idemodthingy.api.IDELanguageHighlight;

public class IDELanguageText implements IDELanguageHighlight {

	@Override
	public String[] tokenize(String text) {
		return new String[] { text };
	}

	@Override
	public int getKeywordColor(String text) {
		return Color.WHITE.getRGB();
	}
	
}

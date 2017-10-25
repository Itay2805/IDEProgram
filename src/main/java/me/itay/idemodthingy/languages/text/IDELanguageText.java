package me.itay.idemodthingy.languages.text;

import java.awt.Color;

import me.itay.idemodthingy.api.IDELanguageHighlight;
import me.itay.idemodthingy.components.IDETextArea;

public class IDELanguageText implements IDELanguageHighlight {

	@Override
	public String[] tokenize(String text) {
		return new String[] { text };
	}

	@Override
	public int getKeywordColor(String text) {
		return Color.WHITE.getRGB();
	}

	@Override
	public void reset() {
		
	}

	@Override
	public void errorCheck(IDETextArea area, String code) {
		
	}
	
}

package me.itay.idemodthingy.languages.text;

import java.awt.Color;
import java.util.TreeMap;

import me.itay.idemodthingy.api.IDELanguageHighlight;
import me.itay.idemodthingy.components.IDETextArea;
import me.itay.idemodthingy.programs.IDE.ProjectFile;

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

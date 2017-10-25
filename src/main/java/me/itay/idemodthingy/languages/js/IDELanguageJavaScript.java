package me.itay.idemodthingy.languages.js;

import java.awt.Color;
import java.util.StringJoiner;
import java.util.TreeMap;

import me.itay.idemodthingy.api.IDELanguageHighlight;
import me.itay.idemodthingy.components.IDETextArea;
import me.itay.idemodthingy.programs.IDE.ProjectFile;

public class IDELanguageJavaScript implements IDELanguageHighlight {

	private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
	private static final String[] DELIMITERS = { "\\s", "\\p{Punct}", "\\p{Digit}+"  };
	
	private static final String DELIMITER;
	static {
		StringJoiner joiner = new StringJoiner("|");
		for(String delim : DELIMITERS) {
			joiner.add(delim);
		}
		DELIMITER = joiner.toString();
	}
	
	@Override
	public String[] tokenize(String text) {
		String[] tokens = text.split(String.format(WITH_DELIMITER, "(" + DELIMITER + ")"));
		return tokens;
	}
	
	private String lastToken = "";
	private boolean quate = false;
	
	@Override
	public int getKeywordColor(String text) {
		if(text.contains("\"")) {
			if(quate) {
				quate = false;
				return COLOR_STRING;
			}
			quate = true;
		}
		if(quate) {
			return COLOR_STRING;
		}
		if(text.length() == 0) {
			return 0;
		}
		int color = 0;
		switch(text) {
			case "break":
			case "with":
			case "do":
			case "case":
			case "else":
			case "catch":
			case "finally":
			case "return":
			case "continue":
			case "switch":
			case "while":
			case "default":
			case "if":
			case "throw":
			case "try":
			case "for":
				color = COLOR_STATEMENT;
				break;
			case "this":
			case "var":
			case "instanceof":
			case "typeof":
			case "debugger":
			case "function":
			case "delete":
			case "in":
			case "null":
			case "true":
			case "false":
			case "void":
			case "new":
				color = COLOR_KEYWORD;
				break;
			default:
				color = COLOR_DEFAULT;
		}
		if(lastToken.equals("function")) {
			color = COLOR_FUNCTION;
		}
		if(lastToken.equals("new")) {
			color = COLOR_TYPE;
		}
		if(isNumeric(text)) {
			color =  COLOR_NUMBER;
		}
		if(color == COLOR_DEFAULT && Character.isAlphabetic(text.charAt(0))) {
			color = COLOR_VARIABLE;
		}
		if(!text.trim().isEmpty()) lastToken = text;
		return color;
	}
	
	public boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}

	@Override
	public void reset() {
		quate = false;
	}

	@Override
	public void errorCheck(IDETextArea area, String code) {
		
	}
	
}

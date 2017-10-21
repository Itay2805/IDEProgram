package me.itay.idemodthingy.languages.js;

import java.awt.Color;
import java.util.StringJoiner;

import me.itay.idemodthingy.api.IDELanguageHighlight;

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
	
	@Override
	public int getKeywordColor(String text) {
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
				color = 0xC582BA;
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
				color = 0x569CD6;
				break;
			default:
				color = 0xD4D4C8;
		}
		if(lastToken.equals("function")) {
			color = 0xDCDCAA;
		}
		if(lastToken.equals("new")) {
			color = 0x49C3A6;
		}
		if(isNumeric(text)) {
			color =  0xB5C378;
		}
		if(color == 0xD4D4C8 && Character.isAlphabetic(text.charAt(0))) {
			color = 0x9CDCFE;
		}
		if(!text.trim().isEmpty()) lastToken = text;
		return color;
	}
	
	public boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}
	
}

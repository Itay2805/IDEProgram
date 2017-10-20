package me.itay.idemodthingy;

import java.awt.Color;

import me.itay.idemodthingy.api.IDELanguageHighlight;

public class JSLanguage implements IDELanguageHighlight {
	
	private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

	
	@Override
	public int getKeywordColor(String text) {
		if(text.equals("var")) {
			return Color.BLUE.getRGB();
		}else if(text.equals("function")) {
			return Color.BLUE.getRGB();
		}else if(text.equals("return")) {
			return Color.BLUE.getRGB();
		}else if(isNumber(text)) {
			return Color.ORANGE.getRGB();
		}else {
			return Color.WHITE.getRGB();
		}
	}
	
	private boolean isNumber(String text) {
		try {
			Integer.parseInt(text);
			return true;
		}catch(Exception e){
			
		}
		return false;
	}

	@Override
	public String[] tokenize(String text) {
		return text.split(String.format(WITH_DELIMITER, "( |\\(|\\)|\\,)"));
	}

}

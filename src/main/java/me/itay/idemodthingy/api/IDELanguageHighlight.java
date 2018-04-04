package me.itay.idemodthingy.api;

import me.itay.idemodthingy.components.IDETextArea;

public interface IDELanguageHighlight {
	
	int COLOR_KEYWORD = 0x569CD6;
	int COLOR_STATEMENT = 0xC582BA;
	int COLOR_FUNCTION = 0xDCDCAA;
	int COLOR_TYPE = 0x49C3A6;
	int COLOR_NUMBER = 0xB5C378;
	int COLOR_VARIABLE = 0x9CDCFE;
	int COLOR_DEFAULT = 0xD4D4C8;
	int COLOR_STRING = 0xCE9172;

	/**
	 * Called after tokenized a got keyword color for every displayed line
	 */
	void reset();
	
	/**
	 * tokenize the line into tokens
	 * 
	 * @param text
	 * @return
	 */
	String[] tokenize(String text);
	
	/**
	 * 
	 * get the keyword color for the token
	 * 
	 * @param text
	 * @return
	 */
	int getKeywordColor(String text);
	
	/**
	 * 
	 * check for error in a file
	 * 
	 */
	void errorCheck(IDETextArea area, String code);
	
}

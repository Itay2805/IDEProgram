package me.itay.idemodthingy.api;

import me.itay.idemodthingy.components.IDETextArea;

public interface IDELanguageHighlight {
	
	public static final int COLOR_KEYWORD = 0x569CD6;
	public static final int COLOR_STATEMENT = 0xC582BA;
	public static final int COLOR_FUNCTION = 0xDCDCAA;
	public static final int COLOR_TYPE = 0x49C3A6;
	public static final int COLOR_NUMBER = 0xB5C378;
	public static final int COLOR_VARIABLE = 0x9CDCFE;
	public static final int COLOR_DEFAULT = 0xD4D4C8;
	public static final int COLOR_STRING = 0xCE9172;

	/**
	 * Called after tokenized a got keyword color for every displayed line
	 */
	public void reset();
	
	/**
	 * tokenize the line into tokens
	 * 
	 * @param text
	 * @return
	 */
	public String[] tokenize(String text);
	
	/**
	 * 
	 * get the keyword color for the token
	 * 
	 * @param text
	 * @return
	 */
	public int getKeywordColor(String text);
	
	/**
	 * 
	 * check for error in a file
	 * 
	 */
	public void errorCheck(IDETextArea area, String code);
	
}

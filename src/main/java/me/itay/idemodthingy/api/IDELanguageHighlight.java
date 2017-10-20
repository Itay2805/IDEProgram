package me.itay.idemodthingy.api;

public interface IDELanguageHighlight {

	public String[] tokenize(String text);
	public int getKeywordColor(String text);
	
}

package com.example.examplemod;

public interface IDELanguage {

	public String[] tokenize(String text);
	public int getKeywordColor(String text);
	
}

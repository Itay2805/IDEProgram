package me.itay.idemodthingy.api;

import me.itay.idemodthingy.programs.bluej.api.SyntaxHighlighter;

public class IDELanguageSupport {
	
	private SyntaxHighlighter highlight;
	private IDELanguageRuntime runtime;
	private String name;
	
	public IDELanguageSupport(String name, SyntaxHighlighter highlight, IDELanguageRuntime runtime) {
		this.highlight = highlight;
		this.runtime = runtime;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public SyntaxHighlighter getHighlight() {
		return highlight;
	}
	
	public IDELanguageRuntime getRuntime() {
		return runtime;
	}
	
}

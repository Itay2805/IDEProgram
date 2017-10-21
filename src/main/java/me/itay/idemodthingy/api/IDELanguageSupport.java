package me.itay.idemodthingy.api;

public class IDELanguageSupport {
	
	private IDELanguageHighlight highlight;
	private IDELanguageRuntime runtime;
	private String name;
	
	public IDELanguageSupport(String name, IDELanguageHighlight highlight, IDELanguageRuntime runtime) {
		this.highlight = highlight;
		this.runtime = runtime;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public IDELanguageHighlight getHighlight() {
		return highlight;
	}
	
	public IDELanguageRuntime getRuntime() {
		return runtime;
	}
	
}

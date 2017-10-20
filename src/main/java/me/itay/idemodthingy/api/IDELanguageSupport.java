package me.itay.idemodthingy.api;

public class IDELanguageSupport {
	
	private IDELanguageHighlight highlight;
	
	public IDELanguageSupport(IDELanguageHighlight highlight) {
		this.highlight = highlight;
	}
	
	public IDELanguageHighlight getHighlight() {
		return highlight;
	}
	
}

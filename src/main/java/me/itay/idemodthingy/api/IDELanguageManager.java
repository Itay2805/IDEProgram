package me.itay.idemodthingy.api;

import java.util.HashMap;

public class IDELanguageManager {
	
	private static HashMap<String, IDELanguageSupport> support = new HashMap<>();
	
	public static void addSupport(String name, IDELanguageSupport support) {
		IDELanguageManager.support.put(name, support);
	}
	
	public static HashMap<String, IDELanguageSupport> getSupport() {
		return support;
	}
	
}

package me.itay.idemodthingy.api;

import java.io.PrintStream;
import java.util.TreeMap;

import com.mrcrayfish.device.api.app.Application;

public interface IDELanguageRuntime {
	
	public String exe(Application app, PrintStream out, TreeMap<String, String> files);
	
}

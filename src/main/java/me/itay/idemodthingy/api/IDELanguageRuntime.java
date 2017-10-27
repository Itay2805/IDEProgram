package me.itay.idemodthingy.api;

import java.io.PrintStream;
import java.util.TreeMap;

import com.mrcrayfish.device.api.app.Application;

import me.itay.idemodthingy.programs.OLDIDE.ProjectFile;

public interface IDELanguageRuntime {
	
	/**
	 * execute the code
	 * 
	 * @param app the Application that runs the code
	 * @param out the output stream
	 * @param files the files in the project
	 * @return error if any
	 */
	public String exe(Application app, PrintStream out, TreeMap<String, ProjectFile> files);
	
}

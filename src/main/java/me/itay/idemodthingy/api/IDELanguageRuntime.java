package me.itay.idemodthingy.api;

import java.io.PrintStream;
import java.util.TreeMap;

import org.lwjgl.util.glu.Project;

import com.mrcrayfish.device.api.app.Application;

import me.itay.idemodthingy.programs.IDE.ProjectFile;

public interface IDELanguageRuntime {
	
	public String exe(Application app, PrintStream out, TreeMap<String, ProjectFile> files);
	
}

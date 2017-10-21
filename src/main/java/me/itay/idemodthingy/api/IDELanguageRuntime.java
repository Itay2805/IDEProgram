package me.itay.idemodthingy.api;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.mrcrayfish.device.api.app.Application;

public interface IDELanguageRuntime {
	
	public String exe(Application app, PrintStream out, String code);
	
}

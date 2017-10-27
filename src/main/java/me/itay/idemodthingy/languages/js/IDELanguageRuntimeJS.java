package me.itay.idemodthingy.languages.js;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.TreeMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.mrcrayfish.device.api.app.Application;

import me.itay.idemodthingy.api.IDELanguageRuntime;
import me.itay.idemodthingy.programs.IDE.ProjectFile;

import static me.itay.idemodthingy.util.StringUtil.convertStreamToString;

public class IDELanguageRuntimeJS implements IDELanguageRuntime {
	
	private static String bootstrap;
	private static String opengl;
	static {
		InputStream stream = IDELanguageRuntimeJS.class.getResourceAsStream("/assets/idemodthingy/scripts/js/bootstrap.js");
		bootstrap = convertStreamToString(stream);

		stream = IDELanguageRuntimeJS.class.getResourceAsStream("/assets/idemodthingy/scripts/js/opengl.js");
		opengl = convertStreamToString(stream);
	}
	
	@Override
	public String exe(Application app, PrintStream out, TreeMap<String, ProjectFile> files) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			engine.put("runtime", new RuntimeFeatures(app, out, files));
			engine.eval(bootstrap);
			engine.eval(opengl);
			System.out.println(files.firstEntry());
			if(!files.isEmpty()) engine.eval(files.firstEntry().getValue().code);
			return null;
		} catch (Throwable e) {
			return e.getMessage().replaceAll("", "");
		}
	}
	
	
	
}

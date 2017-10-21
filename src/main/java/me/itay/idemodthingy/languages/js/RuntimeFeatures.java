package me.itay.idemodthingy.languages.js;

import java.io.PrintStream;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Button;

import me.itay.idemodthingy.Runner;
import scala.util.control.Exception;

public class RuntimeFeatures {
	
	private PrintStream out;
	private Application application;
	
	public RuntimeFeatures(Application application, PrintStream out) {
		this.out = out;
		this.application = application;
	}

	public void print(Object output) {
		out.println(output.toString());
	}
	
	public Button createButton(String text, int x, int y, int width, int height) {
		System.out.println(width + ", " + height);
		return new Button(text, x, y, width, height);
	}
	
	public void addComponent(Component component) {
		if(application instanceof Runner) {
			((Runner)application).addDynamicComponent(component);
		}else {
			throw new RuntimeException("Can not add components while in IDE");
		}
	}

}

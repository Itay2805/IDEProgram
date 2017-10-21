package me.itay.idemodthingy.languages.js;

import java.io.PrintStream;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.listener.ClickListener;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
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
		return new Button(text, x, y, width, height);
	}
	
	public void addComponent(Component component) {
		if(application instanceof Runner) {
			((Runner)application).addDynamicComponent(component);
		}else {
			throw new RuntimeException("Can not add components while in IDE");
		}
	}
	
	public void message(Object text, Object title) {
		Message msg = new Message(text.toString());
		msg.setTitle(title.toString());
		application.openDialog(msg);
	}
	
	public void button_setClickListener(Object btn, Runnable mirror) {
		if(btn instanceof Button) {			
			((Button)btn).setClickListener(new ClickListener() {
				@Override
				public void onClick(Component c, int mouseButton) {
					mirror.run();
				}
			});
		}else {
			throw new RuntimeException("Not a native button");
		}
	}

}

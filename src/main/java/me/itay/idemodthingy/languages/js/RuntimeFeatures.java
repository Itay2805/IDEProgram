package me.itay.idemodthingy.languages.js;

import java.io.PrintStream;
import java.util.TreeMap;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import me.itay.idemodthingy.components.GLCanvas;
import me.itay.idemodthingy.programs.Runner;
import scala.annotation.meta.field;
import scala.collection.immutable.RedBlackTree.Tree;
import scala.util.control.Exception;

public class RuntimeFeatures {
	
	private PrintStream out;
	private Application application;
	private TreeMap<String, String> files;
	
	public RuntimeFeatures(Application application, PrintStream out, TreeMap<String, String> files) {
		this.out = out;
		this.application = application;
		this.files = files;
	}

	public String getJSFile(String file) {
		return files.get(file);
	}
	
	public void print(Object output) {
		out.println(output.toString());
	}
	
	public Button createButton(String text, int x, int y, int width, int height) {
		return new Button(text, x, y, width, height);
	}
	
	public GLCanvas createGLCanvas(int x, int y, int width, int height) {
		return new GLCanvas(x, y, width, height);
	}
	
	public TextArea createTextArea(int x, int y, int width, int height) {
		return new TextArea(x, y, width, height);
	}
	
	public TextField createTextFeild(int x, int y, int width) {
		return new TextField(x, y, width);
	}
	
	public Label createLabel(String text, int x, int y) {
		return new Label(text, x, y);
	}
	
	public void addComponent(Component component) {
		if(application instanceof Runner) {
			((Runner)application).addDynamicComponent(component);
		}else {
			throw new RuntimeException("Can not add components while in IDE");
		}
	}
	
	public void onRender(Runnable mirror) {
		if(application instanceof Runner) {
			((Runner)application).setOnRender(mirror);
		}else {
			throw new RuntimeException("Can not set on render while in IDE");
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

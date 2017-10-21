package me.itay.idemodthingy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Dialog.Input;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.ResponseHandler;
import com.mrcrayfish.device.api.app.Dialog.SaveFile;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.io.File;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import me.itay.idemodthingy.components.IDETextArea;
import net.minecraft.nbt.NBTTagCompound;

public class IDE extends Application {

	private Button language;
	private Button run;
	private Button save;
	private Button load;
	private IDETextArea text;
	
	private IDELanguageSupport support = IDELanguageManager.getSupport().get("text");
	
	private static final int WIDTH 	= 360;
	private static final int HEIGHT 	= 160;
	
	private static final int BOUNDS_SIZE = 5;
	private static final int BUTTONS_HEIGHT = 15;
	
	@Override
	public void init() {
		setDefaultWidth(360);
		setDefaultHeight(160);
		
		language = new Button("Lang", BOUNDS_SIZE, BOUNDS_SIZE, BUTTONS_HEIGHT * 2, BUTTONS_HEIGHT);
		run = new Button("Run", BUTTONS_HEIGHT * 2 + BOUNDS_SIZE * 2, BOUNDS_SIZE, BUTTONS_HEIGHT * 2, BUTTONS_HEIGHT);
		save = new Button("Save", BUTTONS_HEIGHT * 4 + BOUNDS_SIZE * 3, BOUNDS_SIZE, BUTTONS_HEIGHT * 2, BUTTONS_HEIGHT);
		load = new Button("Load", BUTTONS_HEIGHT * 6 + BOUNDS_SIZE * 4, BOUNDS_SIZE, BUTTONS_HEIGHT * 2, BUTTONS_HEIGHT);
		text = new IDETextArea(BOUNDS_SIZE, BOUNDS_SIZE * 2 + BUTTONS_HEIGHT, WIDTH - (BOUNDS_SIZE * 2), HEIGHT - (BOUNDS_SIZE * 3 + BUTTONS_HEIGHT), support.getHighlight());
		
		addComponent(load);
		addComponent(save);
		addComponent(run);
		addComponent(language);
		addComponent(text);
		
		IDE curr = this;

		run.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				if(support.getRuntime() == null) {
					Message msg = new Message("This language has no runtime support!");
					msg.setTitle("Error");
					curr.openDialog(msg);
				}else {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream stream = new PrintStream(baos);
					String error = support.getRuntime().exe(curr, stream, text.getText());
					if(error != null) {
						Message msg = new Message(error);
						msg.setTitle("Error");
						curr.openDialog(msg);
					}else {
						String output = baos.toString();
						if(output.trim().isEmpty()) {
							output = "Run program succesfully!";
						}
						output = output.replaceAll("\r", "");
						Message msg = new Message(output);
						msg.setTitle("Output");
						curr.openDialog(msg);						
					}
				}
			}
		});
		
		language.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				Input input = new Input();
				input.setResponseHandler(new ResponseHandler<String>() {
					@Override
					public boolean onResponse(boolean success, String e) {
						if(success) {
							IDELanguageSupport lang = IDELanguageManager.getSupport().get(e);
							if(lang == null) {
								Message msg = new Message("Unknown Language");
								msg.setTitle("Error");
								curr.openDialog(msg);
							}else {
								curr.support = lang;
								text.setLanguage(curr.support.getHighlight());
							}
						}
						return true;
					}
				});
				curr.openDialog(input);
			}
		});
		
		load.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				OpenFile file = new OpenFile(curr);
				file.setResponseHandler(new ResponseHandler<File>() {
					@Override
					public boolean onResponse(boolean success, File e) {
						NBTTagCompound data = e.getData();
						if(data.hasKey("code")) {							
							String code = data.getString("code");
							text.setText(code);
						}
						if(data.hasKey("lang")) {
							String langName = data.getString("lang");
							IDELanguageSupport lang = IDELanguageManager.getSupport().get(langName);
							if(lang == null) {
								Message msg = new Message("Unknown Language");
								msg.setTitle("Error");
								curr.openDialog(msg);
							}else {
								curr.support = lang;
								text.setLanguage(curr.support.getHighlight());
							}
						}
						return true;
					}
				});
				curr.openDialog(file);
			}
		});
		
		save.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				NBTTagCompound data = new NBTTagCompound();
				data.setString("code", text.getText());
				data.setString("lang", support.getName());
				SaveFile file = new SaveFile(curr, new File("", curr, data));
				file.setResponseHandler(new ResponseHandler<File>() {
					@Override
					public boolean onResponse(boolean success, File e) {
						return true;
					}
				});
				curr.openDialog(file);
			}
		});
	}

	@Override
	public void load(NBTTagCompound tagCompound) {
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
		
	}

}

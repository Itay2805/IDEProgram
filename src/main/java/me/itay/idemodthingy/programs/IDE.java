package me.itay.idemodthingy.programs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog.Input;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.ResponseHandler;
import com.mrcrayfish.device.api.app.Dialog.SaveFile;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ComboBox.List;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.listener.ChangeListener;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.io.File;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import me.itay.idemodthingy.components.IDETextArea;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class IDE extends Application {

	private List<String> language;
	private Button run;
	private Button save;
	private Button load;
	private Button newFile, deleteFile;
	private ItemList<String> filesList;
	private IDETextArea text;
	
	private IDELanguageSupport support;
	
	private TreeMap<String, String> files;
	
	private static final int WIDTH 	= 360;
	private static final int HEIGHT 	= 160;
	
	private static final int BOUNDS_SIZE = 5;
	private static final int BUTTONS_HEIGHT = 15;
	
	private String currentFile = null;
	
	@Override
	public void init() {
		setDefaultWidth(360);
		setDefaultHeight(160);
		
		files = new TreeMap<>();
		
//		language = new Button("Lang", , BUTTONS_HEIGHT);
		
		String[] languages = new String[IDELanguageManager.getSupport().size()];
		int i = 0;
		for(String key : IDELanguageManager.getSupport().keySet()) {
			languages[i] = key;
			i++;
		}
		System.out.println(Arrays.toString(languages));
		language = new List<>(BOUNDS_SIZE, BOUNDS_SIZE, 80, languages);
		
		support = IDELanguageManager.getSupport().get(language.getSelectedItem());
		
		run = new Button("Run", 80 + BOUNDS_SIZE * 2, BOUNDS_SIZE, 30, BUTTONS_HEIGHT);
		newFile = new Button("New", (80 + 30) + BOUNDS_SIZE * 3, BOUNDS_SIZE, 30, BUTTONS_HEIGHT);
		deleteFile = new Button("Del", (80 + 60) + BOUNDS_SIZE * 3, BOUNDS_SIZE, 30, BUTTONS_HEIGHT);
		save = new Button("Save", (80 + 90) + BOUNDS_SIZE * 3, BOUNDS_SIZE, 30, BUTTONS_HEIGHT);
		load = new Button("Load", (80 + 120) + BOUNDS_SIZE * 4, BOUNDS_SIZE, 30, BUTTONS_HEIGHT);
		
		filesList = new ItemList<>(BOUNDS_SIZE, BOUNDS_SIZE * 2 + BUTTONS_HEIGHT, 80, ((HEIGHT - (BOUNDS_SIZE * 3 + BUTTONS_HEIGHT)) / 15) + 1);
		text = new IDETextArea(80 + 2 * BOUNDS_SIZE, BOUNDS_SIZE * 2 + BUTTONS_HEIGHT, WIDTH - (BOUNDS_SIZE * 3 + 80), HEIGHT - (BOUNDS_SIZE * 3 + BUTTONS_HEIGHT), support.getHighlight());
		
		text.setEditable(false);
		
		addComponent(load);
		addComponent(save);
		addComponent(run);
		addComponent(language);
		addComponent(filesList);
		addComponent(text);
		addComponent(deleteFile);
		addComponent(newFile);
		
		IDE curr = this;

		newFile.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				Input input = new Input("File name");
				input.setResponseHandler(new ResponseHandler<String>() {
					@Override
					public boolean onResponse(boolean success, String e) {
						if(files.containsKey(e)) {
							Message msg = new Message("This file already exists!");
							msg.setTitle("Error");
							curr.openDialog(msg);
							return true;
						}
						files.put(e, "");
						System.out.println("ADD: " + files);
						filesList.addItem(e);
						return true;
					}
				});
				curr.openDialog(input);
			}
		});
		
		filesList.setItemClickListener(new ItemClickListener<String>() {
			@Override
			public void onClick(String e, int index, int mouseButton) {
				if(currentFile != null) {
					String oldCode = text.getText();
					files.replace(currentFile, oldCode);
					System.out.println("FL1: " + files);
				}
				String code = files.get(filesList.getSelectedItem());
				System.out.println("FL2: " + files);
				text.setText(code);
				text.setEditable(true);
				currentFile = filesList.getSelectedItem();
			}
		});
		
		run.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				if(currentFile != null) {
					String oldCode = text.getText();
					files.replace(currentFile, oldCode);
				}
				if(support.getRuntime() == null) {
					Message msg = new Message("This language has no runtime support!");
					msg.setTitle("Error");
					curr.openDialog(msg);
				}else {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream stream = new PrintStream(baos);
					String error = support.getRuntime().exe(curr, stream, files);
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
		
		language.setChangeListener(new ChangeListener<String>() {
			@Override
			public void onChange(String oldValue, String newValue) {
				IDELanguageSupport lang = IDELanguageManager.getSupport().get(newValue);
				if(lang == null) {
					Message msg = new Message("Unknown Language");
					msg.setTitle("Error");
					curr.openDialog(msg);
				}else {
					curr.support = lang;
					text.setLanguage(curr.support.getHighlight());
				}				
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
						System.out.println(data);
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
				
				// save language
				data.setString("lang", support.getName());
				
				// save files
				NBTTagList list = new NBTTagList();
				for(String file : files.keySet()) {
					NBTTagString str = new NBTTagString(files.get(file));
					list.appendTag(str);
				}
				
				data.setTag("files", list);
				
				SaveFile file = new SaveFile(curr, new File("", curr, data));
				file.setResponseHandler(new ResponseHandler<File>() {
					@Override
					public boolean onResponse(boolean success, File e) {
						return success;
					}
				});
				curr.openDialog(file);
			}
		});
	}
	
	@Override
	public boolean handleFile(File file) {
		NBTTagCompound data = file.getData();
		System.out.println(data);
		return true;
	}
	
	@Override
	public void load(NBTTagCompound tagCompound) {
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
	}

}

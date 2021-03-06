package me.itay.idemodthingy.programs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog.Input;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.SaveFile;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ComboBox.List;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.io.File;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import me.itay.idemodthingy.components.IDETextArea;
import me.itay.idemodthingy.languages.text.IDELanguageText;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class OLDIDE extends Application {

	private List<String> language;
	private Button run;
	private Button save;
	private Button load;
	private Button newFile, deleteFile;
	private ItemList<String> filesList;
	private IDETextArea text;
	
	private TreeMap<String, ProjectFile> files;
	private File saveTo;
	
	private static final int WIDTH 	= 360;
	private static final int HEIGHT 	= 160;
	
	private static final int BOUNDS_SIZE = 5;
	private static final int BUTTONS_HEIGHT = 15;
	
	private ProjectFile currentFile = null;
	
	public static class ProjectFile {
		public String fileName;
		public String code;
		public IDELanguageSupport support;
		
		public ProjectFile(String fileName, String code, IDELanguageSupport support) {
			this.fileName = fileName;
			this.code = code;
			this.support = support;
		}

		@Override
		public String toString() {
			return "ProjectFile [fileName=" + fileName + ", code=" + code + ", support=" + support + "]";
		}
	}
	
	private void loadProject(File file) {
		saveTo = file;
		
		files.clear();
		filesList.setItems(new ArrayList<>());
		
		NBTTagCompound data = file.getData();
		
		NBTTagList list = data.getTagList("files", NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound comp = list.getCompoundTagAt(i);
			String lang = comp.getString("lang");
			String code = comp.getString("code");
			String name = comp.getString("name");
			
			ProjectFile pfile = new ProjectFile(name, code, IDELanguageManager.getSupport().get(lang));
			files.put(name, pfile);
			filesList.addItem(name);
		}
	}
	
	private void setCurrentFile(ProjectFile file) {
		currentFile = file;
		text.setLanguage(file.support.getHighlight());
		text.setText(file.code);
		text.setEditable(true);
		language.setEnabled(true);
	}
	
	@Override
	public void init() {
		saveTo = null;
		
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

//				support = IDELanguageManager.getSupport().get(language.getSelectedItem());
		
		run = new Button(80 + BOUNDS_SIZE * 2, BOUNDS_SIZE, 30, BUTTONS_HEIGHT, "Run");
		newFile = new Button((80 + 30) + BOUNDS_SIZE * 3, BOUNDS_SIZE, 30, BUTTONS_HEIGHT, "New");
		deleteFile = new Button((80 + 60) + BOUNDS_SIZE * 3, BOUNDS_SIZE, 30, BUTTONS_HEIGHT, "Del");
		save = new Button((80 + 90) + BOUNDS_SIZE * 3, BOUNDS_SIZE, 30, BUTTONS_HEIGHT, "Save");
		load = new Button((80 + 120) + BOUNDS_SIZE * 4, BOUNDS_SIZE, 30, BUTTONS_HEIGHT, "Load");
		
		filesList = new ItemList<>(BOUNDS_SIZE, BOUNDS_SIZE * 2 + BUTTONS_HEIGHT, 80, ((HEIGHT - (BOUNDS_SIZE * 3 + BUTTONS_HEIGHT)) / 15) + 1);
//		text = new IDETextArea(80 + 2 * BOUNDS_SIZE, BOUNDS_SIZE * 2 + BUTTONS_HEIGHT, WIDTH - (BOUNDS_SIZE * 3 + 80), HEIGHT - (BOUNDS_SIZE * 3 + BUTTONS_HEIGHT), this.);
		
		text.setEditable(false);
		
		addComponent(load);
		addComponent(save);
		addComponent(run);
		addComponent(language);
		addComponent(filesList);
		addComponent(text);
		addComponent(deleteFile);
		addComponent(newFile);
		
		OLDIDE curr = this;

		newFile.setClickListener((x, y, b)-> {
			Input input = new Input("File name");
			input.setResponseHandler((success, e)->{
				if(files.containsKey(e)) {
					Message msg = new Message("This file already exists!");
					msg.setTitle("Error");
					curr.openDialog(msg);
					return true;
				}
				ProjectFile file = new ProjectFile(e, "", IDELanguageManager.getSupport().get(language.getSelectedItem()));
				files.put(e, file);
				filesList.addItem(e);
				setCurrentFile(file);
				return true;
			});
			curr.openDialog(input);
		});
		
		deleteFile.setClickListener((x, y, b)-> {
			ProjectFile f = files.get(filesList.getSelectedItem());
			filesList.removeItem(filesList.getSelectedIndex());
			if(f == currentFile) {
				text.setText("");
				text.setEditable(false);
			}
			files.remove(f.fileName);
			currentFile = null;
		});
		
		filesList.setItemClickListener((e, index, mouseButton)-> {
			if(currentFile != null) {
				currentFile.code = text.getText();
			}
			setCurrentFile(files.get(filesList.getSelectedItem()));
		});
		
		run.setClickListener((x, y, b)->{
			if(currentFile != null) {
				currentFile.code = text.getText();
			}
			if(files.firstEntry().getValue().support.getRuntime() == null) {
				Message msg = new Message("This language has no runtime support!");
				msg.setTitle("Error");
				curr.openDialog(msg);
			}else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream stream = new PrintStream(baos);
				/*String error = files.firstEntry().getValue().support.getRuntime().exe(curr, stream, files);
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
				}*/
			}
		});
		
		language.setChangeListener((oldValue, newValue)->{
			IDELanguageSupport lang = IDELanguageManager.getSupport().get(newValue);
			if(lang == null) {
				Message msg = new Message("Unknown Language");
				msg.setTitle("Error");
				curr.openDialog(msg);
			}else {
				if(currentFile == null){
					Message msg = new Message("No file to apply language to!");
					msg.setTitle("Error");
					curr.openDialog(msg);
				}else {
					currentFile.support = lang;
					text.setLanguage(currentFile.support.getHighlight());
				}
			}
		});
		
		load.setClickListener((x, y, b)-> {
			OpenFile file = new OpenFile(curr);
			file.setResponseHandler((success, e)-> {
					loadProject(e);
					return true;
			});
			curr.openDialog(file);
		});
		
		save.setClickListener((x, y, b)-> {
			currentFile.code = text.getText();

			NBTTagCompound data = new NBTTagCompound();

			// save files
			NBTTagList list = new NBTTagList();
			for(String file : files.keySet()) {
				NBTTagCompound comp = new NBTTagCompound();
				ProjectFile pfile = files.get(file);
				comp.setString("code", pfile.code);
				comp.setString("lang", pfile.support.getName());
				comp.setString("name", pfile.fileName);
				list.appendTag(comp);
			}
			data.setTag("files", list);

			if(saveTo != null) {
				saveTo.setData(data);
			}else {
				SaveFile file = new SaveFile(curr, new File("", curr, data));
				file.setResponseHandler((success, e)->true);
				curr.openDialog(file);
			}
		});
	}
	
	@Override
	public boolean handleFile(File file) {
		loadProject(file);
		return true;
	}
	
	@Override
	public void load(NBTTagCompound tagCompound) {
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
	}

}

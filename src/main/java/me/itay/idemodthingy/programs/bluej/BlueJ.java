package me.itay.idemodthingy.programs.bluej;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog.Confirmation;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.SaveFile;
import com.mrcrayfish.device.api.app.Icon;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.io.File;

import me.itay.idemodthingy.programs.bluej.components.BlueJCodeEditor;
import me.itay.idemodthingy.programs.bluej.resources.BlueJResourceLocation;
import net.minecraft.nbt.NBTTagCompound;

public class BlueJ extends Application {
	
	private static final int WIDTH = 362, HEIGHT = 164;
	
	private Button newProject, openProject, saveFile, exportProject;
	private Button newFile, deleteFile;
	private Button copyAll, paste;
	// TODO Import and export resources
	private Button run, stop;
	private Button settings;
	
	private BlueJCodeEditor codeEditor;
	private ItemList<String> files;
	// TODO add resources
	// TODO add terminal
	
	private Project currentProject;
	private ProjectFile openedFile;
	private int openedFileHash;
	
	private int leftPanelWidth;
	private int middlePanelWidth;
//	private int rightPanelWidth;
	
	private int x;
	
	private void resetLayout() {
		x = 1;
		leftPanelWidth = 80;
		middlePanelWidth = 280;
//		rightPanelWidth = 0;
	}
	
	private int getNextBtnPos() {
		int curr = x;
		x += 16;
		return curr;
	}
	
	private void addSeperator() {
		x += 2;
	}
	
	@Override
	public void init() {
		currentProject = null;
		openedFile = null;
		
		setDefaultWidth(WIDTH);
		setDefaultHeight(HEIGHT);
		
		// setup buttons
		
		resetLayout();
		
		newProject = new Button(getNextBtnPos(), 1, Icon.NEW_FOLDER);
		newProject.setToolTip("New Project", "Create new project");
		newProject.setClickListener(this::createProjectHandler);
		openProject = new Button(getNextBtnPos(), 1, Icon.LOAD);
		openProject.setToolTip("Open Project", "Open an exsting project");
		openProject.setClickListener(this::loadProjectHandler);
		exportProject = new Button(getNextBtnPos(), 1, Icon.EXPORT);
		exportProject.setToolTip("Export Project", "Export the project as a runnable");
		exportProject.setClickListener(this::archiveProjectHandler);
		exportProject.setEnabled(false);
		
		addComponent(newProject);
		addComponent(openProject);
		addComponent(exportProject);
		
		addSeperator();
		
		newFile = new Button(getNextBtnPos(), 1, Icon.NEW_FILE);
		newFile.setToolTip("New File", "Create new file");
		newFile.setClickListener(this::newFileHandler);
		deleteFile = new Button(getNextBtnPos(), 1, Icon.TRASH);
		deleteFile.setToolTip("Delete File", "Delete selected file");
		deleteFile.setClickListener(this::deleteFileHandler);
		deleteFile.setEnabled(false);
		saveFile = new Button(getNextBtnPos(), 1, Icon.SAVE);
		saveFile.setClickListener(this::saveFileHandler);
		saveFile.setToolTip("Save File", "Save current file");
		saveFile.setEnabled(false);
		
		addComponent(newFile);
		addComponent(deleteFile);
		addComponent(saveFile);

		addSeperator();
		
		copyAll = new Button(getNextBtnPos(), 1, Icon.COPY);
		copyAll.setToolTip("Copy All", "Copy all the contents of the current file to the clipboard");
		copyAll.setEnabled(false);
		paste = new Button(getNextBtnPos(), 1, Icon.CLIPBOARD);
		paste.setToolTip("Paste", "Paste the contents of the clipboard to the current file");
		paste.setEnabled(false);
		
		addComponent(copyAll);
		addComponent(paste);
		
		addSeperator();
		
		run = new Button(getNextBtnPos(), 1, Icon.PLAY);
		run.setToolTip("Run", "Run code");
		stop = new Button(getNextBtnPos(), 1, Icon.STOP);
		stop.setToolTip("Stop", "Stop running code");
		
		addComponent(run);
		addComponent(stop);
		
		addSeperator();

		settings = new Button(getNextBtnPos(), 1, Icon.WRENCH);
		settings.setToolTip("Settings", "Open and edit settings");
		
		addComponent(settings);
		
		// setup layout
		
		files = new ItemList<>(1, 18, leftPanelWidth, (HEIGHT - 18) / 15 + 1);
		files.setItemClickListener(this::fileBrowserClickHandler);
		codeEditor = new BlueJCodeEditor(1 + leftPanelWidth, 18, middlePanelWidth, HEIGHT - 23, null);
		
		addComponent(files);
		addComponent(codeEditor);

		// set project buttons to disabled
		
		setProjectButtons(false);
	}
	
	////////////////////////// Project Buttons Handlers //////////////////////////
	
	private void createProjectHandler(Component c, int button) {
			currentProject = new Project();
			SaveFile input = new SaveFile(this, new NBTTagCompound());
			input.setResponseHandler((success, file)->{
				if(success) {
					unloadProject(() -> {
						BlueJResourceLocation resloc = new BlueJResourceLocation("files", "root", file.getPath());
						currentProject = Project.loadProject(resloc);
					});
				}
				return true;
			});
			openDialog(input);
	}
	
	private void loadProjectHandler(Component c, int button) {
		unloadProject(() -> {
			OpenFile open = new OpenFile(this);
			open.setResponseHandler((success, file) -> {
				if(success && file != null) {
					handleFile(file);
				}
				return true;
			});
			openDialog(open);
		});
	}
	
	private void archiveProjectHandler(Component c, int button) {
		NBTTagCompound tag = currentProject.archive();
		// TODO: Change from Runner to a newer once we have newer one
		SaveFile file = new SaveFile(this, tag);
		file.setResponseHandler((success, f) -> {
			try {
				Field field = File.class.getDeclaredField("openingApp");
				field.setAccessible(true);
				field.set(f, "idemodthingy:dynamicapp");
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return true;
		});
		openDialog(file);
	}
	
	////////////////////////// File Buttons Handlers //////////////////////////
	
	private void newFileHandler(Component c, int button) {
		AddFileDialog input = new AddFileDialog("File name");
		input.setResponseHandler((success, file) -> {
			if(success) {
				files.addItem(file.getName());
				currentProject.addFile(file);
			}
			return true;
		});
		openDialog(input);
	}
	
	private void deleteFileHandler(Component c, int button) {
		files.removeItem(files.getSelectedIndex());
		deleteFile.setEnabled(false);
		codeEditor.setEditable(false);
		codeEditor.setText("");
		files.setSelectedIndex(-1);
		currentProject.removeFile(openedFile.getName());
		openedFile = null;
	}
	
	private void saveFileHandler(Component c, int button) {
		saveOpenedFile();
	}
	
	////////////////////////// Other Handlers //////////////////////////

	private void fileBrowserClickHandler(String item, int index, int button) {
		unloadFile(() -> {
			if(openedFile != null) {
				openedFile.setCode(codeEditor.getText());
			}
			openedFile = currentProject.getFile(item);
			codeEditor.setText(openedFile.getCode());
//			codeEditor.setLanguage(file.getLanguage().getHighlight());
			
			openedFileHash = codeEditor.getText().hashCode();
			
			saveFile.setEnabled(true);
			codeEditor.setEditable(true);
			deleteFile.setEnabled(true);
		});
	}
	
	////////////////////////// Utility Methods //////////////////////////
	
	@Override
	public boolean handleFile(File file) {
		unloadProject(() -> {
			currentProject = Project.loadProject(new BlueJResourceLocation("files", "root", file.getParent().getPath()));
			
			for(String fileName : currentProject.getAllFileNames()) {
				files.addItem(fileName);
			}
			
			setProjectButtons(true);
		});
		return true;
	}
	
	public void unloadFile(Runnable runnable) {
		if(openedFile != null && codeEditor.getText().hashCode() != openedFileHash) {
			Confirmation shouldSave = new Confirmation("Do you want to save before exiting?");
			shouldSave.setPositiveListener((c, button) -> {
				saveOpenedFile();
				runnable.run();
			});
			shouldSave.setNegativeListener((c, button) -> {
				runnable.run();
			});
			openDialog(shouldSave);
		}else {
			runnable.run();
		}
	}
	
	public void unloadProject(Runnable runnable) {
		unloadFile(() -> {
			currentProject = null;
			openedFile = null;
			
			files.setItems(new ArrayList<String>());
			codeEditor.setText("");
			
			setProjectButtons(false);
			codeEditor.setEditable(false);
			deleteFile.setEnabled(false);
			
			runnable.run();
		});
	}
	
	public void saveOpenedFile() {
		if(openedFile != null && codeEditor.getText().hashCode() != openedFileHash) {
			openedFile.setCode(codeEditor.getText());
			currentProject.getResolvedResourceLocation().getFile(openedFile.getName()).setData(openedFile.toNBT());
			openedFileHash = codeEditor.getText().hashCode();
		}
	}

	public void setProjectButtons(boolean enabled) {
		newFile.setEnabled(enabled);
		files.setEnabled(enabled);
		run.setEnabled(enabled);
		stop.setEnabled(enabled);
		exportProject.setEnabled(enabled);
	}
	
	// TODO load layout settings

	public void load(NBTTagCompound arg0) {}
	public void save(NBTTagCompound arg0) {}
	
}

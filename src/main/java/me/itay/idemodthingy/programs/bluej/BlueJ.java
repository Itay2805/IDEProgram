package me.itay.idemodthingy.programs.bluej;

import java.util.ArrayList;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog.Confirmation;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.SaveFile;
import com.mrcrayfish.device.api.app.Icon;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.io.File;

import me.itay.idemodthingy.components.IDETextArea;
import me.itay.idemodthingy.languages.text.IDELanguageText;
import net.minecraft.nbt.NBTTagCompound;

public class BlueJ extends Application {
	
	private static final int WIDTH = 362, HEIGHT = 164;
	
	private Button newProject, openProject, saveProject;
	// TODO Import and export project
	private Button newFile, deleteFile;
	// TODO Import and export resources
	private Button run, stop;
	private Button settings;
	
	private IDETextArea codeEditor;
	private ItemList<String> files;
	// TODO add resources
	// TODO add terminal
	
	private Project currentProject;
	private String openedFile;
	private File saveLocation;
	
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
		saveLocation = null;
		
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
		saveProject = new Button(getNextBtnPos(), 1, Icon.SAVE);
		saveProject.setClickListener(this::saveProjectHandler);
		saveProject.setToolTip("Save Project", "Save current project");

		addComponent(newProject);
		addComponent(openProject);
		addComponent(saveProject);
		
		addSeperator();
		
		newFile = new Button(getNextBtnPos(), 1, Icon.NEW_FILE);
		newFile.setToolTip("New File", "Create new file");
		newFile.setClickListener(this::newFileHandler);
		deleteFile = new Button(getNextBtnPos(), 1, Icon.TRASH);
		deleteFile.setToolTip("Delete File", "Delete selected file");
		deleteFile.setClickListener(this::deleteFileHandler);
		
		addComponent(newFile);
		addComponent(deleteFile);

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
		codeEditor = new IDETextArea(1 + leftPanelWidth, 18, middlePanelWidth, HEIGHT - 23, new IDELanguageText());
		
		addComponent(files);
		addComponent(codeEditor);

		// set project buttons to disabled
		
		setProjectButtons(false);
		
		markDirty();
	}
	
	////////////////////////// Project Buttons Handlers //////////////////////////
	
	private void createProjectHandler(Component c, int button) {
		unloadProject(() -> {
			currentProject = new Project();
			
			SaveFile save = new SaveFile(this, currentProject.toNBT());
			save.setResponseHandler((success, file) -> {
				if(success && file != null) {
					saveLocation = file;

					// enable project buttons
					setProjectButtons(true);
				}else {
					currentProject = null;
				}
				return true;
			});
			openDialog(save);
		});
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
	
	private void saveProjectHandler(Component c, int button) {
		saveProject();
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
		if(files.getSelectedIndex() < 0) {
			deleteFile.setEnabled(false);
			return;
		}
		
		files.removeItem(files.getSelectedIndex());
		deleteFile.setEnabled(false);
		codeEditor.setEditable(false);
		codeEditor.setText("");
		files.setSelectedIndex(-1);
		currentProject.removeFile(openedFile);
		openedFile = null;
	}
	
	////////////////////////// Other Handlers //////////////////////////

	private void fileBrowserClickHandler(String item, int index, int button) {
		if(openedFile != null) {
			currentProject.getFile(openedFile).setCode(codeEditor.getText());
		}
		openedFile = item;
		ProjectFile file = currentProject.getFile(openedFile);
		codeEditor.setText(file.getCode());
		codeEditor.setLanguage(file.getLanguage().getHighlight());
		
		codeEditor.setEditable(true);
		deleteFile.setEnabled(true);
	}
	
	////////////////////////// Utility Methods //////////////////////////
	
	@Override
	public boolean handleFile(File file) {
		currentProject = Project.fromNBT(file.getData());
		
		for(String fileName : currentProject.getAllFileNames()) {
			files.addItem(fileName);
		}
		
		saveLocation = file;
		setProjectButtons(true);
		
		return true;
	}
	
	public void unloadProject(Runnable runnable) {
		// ask to save if project is currently opened
		if(currentProject != null) {
			Confirmation shouldSave = new Confirmation("Do you want to save before exiting?");
			shouldSave.setPositiveListener((c, button) -> {
				saveProject();
				currentProject = null;
				openedFile = null;
				saveLocation = null;
				
				files.setItems(new ArrayList<String>());
				codeEditor.setText("");
				
				setProjectButtons(false);
				codeEditor.setEditable(false);
				deleteFile.setEnabled(false);
				
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
	
	public void saveProject() {
		if(openedFile != null) {
			currentProject.getFile(openedFile).setCode(codeEditor.getText());
		}
		NBTTagCompound data = currentProject.toNBT();
		saveLocation.setData(data);
		Message msg = new Message("Project saved to " + saveLocation.getPath());
		msg.setTitle("Saved");
		openDialog(msg);
	}

	public void setProjectButtons(boolean enabled) {
		saveProject.setEnabled(enabled);
		newFile.setEnabled(enabled);
		files.setEnabled(enabled);
		run.setEnabled(enabled);
		stop.setEnabled(enabled);
	}
	
	// TODO load layout settings

	public void load(NBTTagCompound arg0) {}
	public void save(NBTTagCompound arg0) {}
	
}

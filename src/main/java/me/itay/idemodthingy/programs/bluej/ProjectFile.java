package me.itay.idemodthingy.programs.bluej;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.object.AppInfo;
import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import me.itay.idemodthingy.programs.bluej.resources.BlueJResolvedResloc;
import me.itay.idemodthingy.programs.bluej.resources.BlueJResourceLocation;
import me.itay.idemodthingy.util.StringUtil;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectFile {
	
	private static final String FILENAME_TAG = "name";
	private static final String CODE_TAG = "code";
	private static final String PROJECT_TAG = "project";
	
	private String name;
	private String code;
	private Project parentProject;
	
	public ProjectFile(String name, String code, Project parentProject) {
		this.name = name;
		this.code = code;
		this.parentProject = parentProject;
	}
	
	public void save(BlueJResourceLocation baseFolder) {
		BlueJResolvedResloc resloc = baseFolder.resolve();
		BlueJResolvedResloc file = resloc.getFile(name);
		if(!file.exists()) file.create();
		file.setData(toNBT());
	}
	
	public String getName() {
		return name;
	}

	public String getCode(){
	    return this.code;
    }

	public List<String> getCodeLines() {
        return Arrays.asList(this.code.split("\n"));
	}

	public Project getParentProject(){
	    return parentProject;
    }
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return "ProjectFile [name=" + name + ", code=" + code + ", project=" + this.parentProject.getProjectLanguage() + "]";
	}

	public void setParentProject(Project project){
	    this.parentProject = project;
    }
	
	public static ProjectFile fromNBT(NBTTagCompound data) {
		String name = data.getString(FILENAME_TAG);
		String code = data.getString(CODE_TAG);
		NBTTagCompound project = data.getCompoundTag(PROJECT_TAG);
		return new ProjectFile(name, code, Project.dearchive(project));
	}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound file = new NBTTagCompound();
		file.setString(FILENAME_TAG, name);
		file.setString(CODE_TAG, code);
		file.setTag(PROJECT_TAG, parentProject.archive());
		file.setString(Project.TYPE_TAG, Project.TYPE_CODE_FILE);
		return file;
	}
	
}

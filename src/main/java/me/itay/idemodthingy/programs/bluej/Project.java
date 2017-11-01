package me.itay.idemodthingy.programs.bluej;

import java.util.Set;
import java.util.TreeMap;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import me.itay.idemodthingy.programs.bluej.resources.BlueJResolvedResloc;
import me.itay.idemodthingy.programs.bluej.resources.BlueJResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class Project {
	
	public static final String TYPE_TAG = "type";

	public static final String TYPE_CODE_FILE = "CODE";

	private String name;
	private BlueJResourceLocation path;
	private BlueJResolvedResloc resolved;

	private TreeMap<String, ProjectFile> files = new TreeMap<>();
	
	public void addFile(ProjectFile file) {
		files.put(file.getName(), file);
		BlueJResolvedResloc resolved = getResolvedResourceLocation().getFile(file.getName());
		resolved.create();
		resolved.setData(file.toNBT());
	}
	
	public void removeFile(String name) {
		files.remove(name);
		getResolvedResourceLocation().getFile(name).delete();
	}
	
	public ProjectFile getFile(String name) {
		return files.get(name);
	}
	
	public Set<String> getAllFileNames() {
		return files.keySet();
	}
	
	public NBTTagCompound archive() {
		return getResolvedResourceLocation().getData();
	}
	
	public static Project dearchive(NBTTagCompound data) {
		Folder folder = Folder.fromTag("virtual_directory", data);
		Project project = new Project();
		for(File f : folder.getFiles()) {
			if(f.getData().hasKey(TYPE_TAG, NBT.TAG_STRING) && f.getData().getString(TYPE_TAG).equals(TYPE_CODE_FILE)) {
				project.files.put(f.getName(), ProjectFile.fromNBT(f.getData()));
			}
		}
		return project;
	}
	
	public static Project loadProject(BlueJResourceLocation resloc) {
		BlueJResolvedResloc resolved = resloc.resolve();
		Project proj = new Project();
		proj.path = resloc;
		for(String file : resolved.listFiles()) {
			BlueJResolvedResloc f = resolved.getFile(file);
			if(f.getData().hasKey(TYPE_TAG, NBT.TAG_STRING) && f.getData().getString(TYPE_TAG).equals(TYPE_CODE_FILE)) {
				proj.files.put(f.name(), ProjectFile.fromNBT(f.getData()));
			}
		}
		proj.resolved = proj.getResourceLocation().resolve();
		return proj;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public BlueJResourceLocation getResourceLocation() {
		return new BlueJResourceLocation("project", path, name);
	}
	
	public BlueJResolvedResloc getResolvedResourceLocation() {
		return resolved;
	}

	@Override
	public String toString() {
		return "Project [name=" + name + ", path=" + path + ", resolved=" + resolved + ", files=" + files + "]";
	}
	
}

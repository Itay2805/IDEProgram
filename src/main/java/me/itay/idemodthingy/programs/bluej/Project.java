package me.itay.idemodthingy.programs.bluej;

import java.util.Set;
import java.util.TreeMap;

import me.itay.idemodthingy.programs.bluej.resources.BlueJResolvedResloc;
import me.itay.idemodthingy.programs.bluej.resources.BlueJResourceLocation;
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
	
	public static Project loadProject(BlueJResourceLocation resloc) {
		BlueJResolvedResloc resolved = resloc.resolve();
//		NBTTagCompound projectData = resolved.getData();
		Project proj = new Project();
		proj.path = resloc;
		for(String file : resolved.listFiles()) {
			BlueJResolvedResloc f = resolved.getFile(file);
			if(f.getData().hasKey(TYPE_TAG, NBT.TAG_STRING) && f.getData().getString("type").equals(TYPE_CODE_FILE)) {
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
	
}

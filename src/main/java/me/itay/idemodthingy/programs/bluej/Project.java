package me.itay.idemodthingy.programs.bluej;

import java.util.Set;
import java.util.TreeMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class Project {
	
	private static final String FILES_TAG = "files";
	
	private TreeMap<String, ProjectFile> files = new TreeMap<String, ProjectFile>();
	
	public void addFile(ProjectFile file) {
		files.put(file.getName(), file);
	}
	
	public void removeFile(String name) {
		files.remove(name);
	}
	
	public ProjectFile getFile(String name) {
		return files.get(name);
	}
	
	public Set<String> getAllFileNames() {
		return files.keySet();
	}
	
	public static Project fromNBT(NBTTagCompound data) {
		Project result = new Project();
		
		NBTTagList list = data.getTagList(FILES_TAG, NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			result.addFile(ProjectFile.fromNBT(list.getCompoundTagAt(i)));
		}
		
		return result;
	}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound projectNBT = new NBTTagCompound();
		
		NBTTagList filesList = new NBTTagList();
		for(ProjectFile pfile : files.values()) {
			filesList.appendTag(pfile.toNBT());
		}
		projectNBT.setTag(FILES_TAG, filesList);
		
		return projectNBT;
	}
	
}

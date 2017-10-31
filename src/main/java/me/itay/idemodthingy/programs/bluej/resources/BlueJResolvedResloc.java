package me.itay.idemodthingy.programs.bluej.resources;

import net.minecraft.nbt.NBTTagCompound;

public interface BlueJResolvedResloc {
	
	// maybe more
	String name();
	boolean exists();
	void create();
	void mkdir();
	void delete();
	void setData(NBTTagCompound data);
	NBTTagCompound getData();
	boolean isFile();
	boolean isFolder();
	String[] listFiles();
	String[] listFolders();
	BlueJResolvedResloc getFile(String name);
	
}

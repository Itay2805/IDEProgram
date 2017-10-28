package me.itay.idemodthingy.programs.bluej.resources;

import net.minecraft.nbt.NBTTagCompound;

public interface BlueJResolvedResloc {
	
	// maybe more
	boolean exists();
	void create();
	void mkdir();
	void delete();
	void setData(NBTTagCompound data);
	NBTTagCompound getData();
	boolean isFile();
	boolean isFolder();
	
}

package me.itay.idemodthingy.programs.bluej.resources;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.system.component.FileBrowser;

import net.minecraft.nbt.NBTTagCompound;

public class BlueJFileSystemResloc implements BlueJResolvedResloc {
	
	private boolean exists = true;
	private Folder folder;
	private File file;
	private String path;
	
	public BlueJFileSystemResloc(String path) {
		if(Laptop.getMainDrive() == null) {
			Application temp = new Application() {
				public void save(NBTTagCompound arg0) {}
				public void load(NBTTagCompound arg0) {}
				public void init() {}
			};
			FileBrowser browser = new FileBrowser(0, 0, temp, FileBrowser.Mode.BASIC);
			browser.init(new Layout());
			browser.handleOnLoad();
		}
		this.path = path;
		
		while(Laptop.getMainDrive() == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Drive drive = Laptop.getMainDrive();
		Folder folder = drive.getRoot();
		
		path = path.replace("\\", "/");
		String[] files = path.split("/");
		for(int i = 0; i < files.length - 1; i++) {
			if(files[i].isEmpty()) continue;
			if(folder.hasFolder(files[i])) {
				folder = folder.getFolder(files[i]);
				continue;
			}
			exists = false;
			break;
		}
		if(exists) {
			if(folder.hasFile(files[files.length - 1])) {
				this.file = folder.getFile(files[files.length - 1]);
			}else if(folder.hasFolder(files[files.length - 1])) {
				this.folder = folder.getFolder(files[files.length - 1]);
			}else {
				exists = false;
			}
		}
	}
	
	@Override
	public boolean exists() {
		return exists;
	}

	@Override
	public void setData(NBTTagCompound data) {
		if(isFile()) {
			file.setData(data);
		}
	}

	@Override
	public NBTTagCompound getData() {
		if(isFile()) {
			return file.getData();
		}
		return null;
	}

	@Override
	public void create() {
		if(exists) return;
		
		Drive drive = Laptop.getMainDrive();
		Folder folder = drive.getRoot();
		
		path = path.replace("\\", "/");
		String[] files = path.split("/");
		for(int i = 0; i < files.length - 1; i++) {
			if(files[i].isEmpty()) continue;
			if(!folder.hasFolder(files[i])) {
				folder.add(new Folder(files[i]));
			}
			while(folder.getFolder(files[i]) == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			folder = folder.getFolder(files[i]);
		}
		folder.add(new File(files[files.length - 1], "", new NBTTagCompound()));
		exists = true;
	}
	
	@Override
	public void mkdir() {
		if(exists) return;
		
		Drive drive = Laptop.getMainDrive();
		Folder folder = drive.getRoot();
		
		path = path.replace("\\", "/");
		String[] files = path.split("/");
		for(int i = 0; i < files.length; i++) {
			if(files[i].isEmpty()) continue;
			if(!folder.hasFolder(files[i])) {
				folder.add(new Folder(files[i]));
			}
			while(folder.getFolder(files[i]) == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			folder = folder.getFolder(files[i]);
		}
	}

	@Override
	public void delete() {
		if(exists) {
			if(isFile()) {
				file.delete();
			}else {
				folder.delete();
			}
		}
	}

	@Override
	public boolean isFile() {
		return file != null;
	}

	@Override
	public boolean isFolder() {
		return folder != null;
	}
	
	
	
}

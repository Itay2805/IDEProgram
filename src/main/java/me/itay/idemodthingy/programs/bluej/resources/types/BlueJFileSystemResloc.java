package me.itay.idemodthingy.programs.bluej.resources.types;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.system.component.FileBrowser;

import me.itay.idemodthingy.programs.bluej.resources.BlueJResolvedResloc;
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
		this.path = path.replace("\\", "/");
		if(this.path.startsWith("/")) {
			this.path = this.path.substring(1);
			path = this.path;
		}
		
		while(Laptop.getMainDrive() == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Drive drive = Laptop.getMainDrive();
		Folder folder = drive.getRoot();
		
		String[] files = path.split("/");
		for(int i = 0; i < files.length - 1; i++) {
			if(files[i].isEmpty()) continue;
			if(folder.getFolder(files[i]) != null) {
				folder = folder.getFolder(files[i]);
				continue;
			}
			exists = false;
			break;
		}
		if(exists) {
			File f = folder.getFile(files[files.length - 1]);
			if(f != null && !f.isFolder()) {
				this.file = folder.getFile(files[files.length - 1]);
			} else if(f != null && f.isFolder()) {
				this.folder = folder.getFolder(files[files.length - 1]);
			} else {
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
			while(true) {
				try {
					file.setData(data);
					return;
				}catch(Exception e){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
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
		
		String[] files = path.split("/");
		for(int i = 0; i < files.length - 1; i++) {
			if(files[i].isEmpty()) continue;
			Folder f = folder.getFolder(files[i]);
			if(f == null) {
				f = new Folder(files[i]);
				folder.add(f);
			}
			folder = f;
		}
		File f = new File(files[files.length - 1], "", new NBTTagCompound());
		folder.add(f);
		this.file = f;
		exists = true;
	}
	
	@Override
	public void mkdir() {
if(exists) return;
		
		Drive drive = Laptop.getMainDrive();
		Folder folder = drive.getRoot();
		
		String[] files = path.split("/");
		for(int i = 0; i < files.length; i++) {
			if(files[i].isEmpty()) continue;
			Folder f = folder.getFolder(files[i]);
			if(f == null) {
				f = new Folder(files[i]);
				folder.add(f);
			}
			folder = f;
		}
		exists = true;
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

	@Override
	public String[] listFiles() {
		if(isFolder() && exists) {
			return folder.getFiles().parallelStream().filter((f) -> !f.isFolder()).map((i) -> i.getName()).toArray((s) -> new String[s]);
		}
		return new String[]{};
	}

	@Override
	public String[] listFolders() {
		if(isFolder() && exists) {
			return folder.getFiles().parallelStream().filter((f) -> f.isFolder()).map((i) -> i.getName()).toArray((s) -> new String[s]);
		}
		return new String[]{};
	}

	@Override
	public BlueJResolvedResloc getFile(String name) {
		String p = path;
		if(!p.endsWith("/")) p += "/";
		p += name;
		return new BlueJFileSystemResloc(p);
	}

	@Override
	public String name() {
		return path.split("/")[path.split("/").length - 1];
	}
	
}

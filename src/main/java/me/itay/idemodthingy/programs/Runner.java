package me.itay.idemodthingy.programs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.TreeMap;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.ResponseHandler;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.Laptop;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.programs.OLDIDE.ProjectFile;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class Runner extends Application {
	
	private TreeMap<String, ProjectFile> files;
	private Runnable onRender;
	
	@Override
	public void init() {
		files = new TreeMap<>();
		
		Runner curr = this;
		
		OpenFile file = new OpenFile(this);
		file.setResponseHandler(new ResponseHandler<File>() {
			@Override
			public boolean onResponse(boolean success, File e) {
				NBTTagCompound data = e.getData();
				
				NBTTagList list = data.getTagList("files", NBT.TAG_COMPOUND);
				for(int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound comp = list.getCompoundTagAt(i);
					String lang = comp.getString("lang");
					String code = comp.getString("code");
					String name = comp.getString("name");
					
					ProjectFile pfile = new ProjectFile(name, code, IDELanguageManager.getSupport().get(lang));
					files.put(name, pfile);
				}
				
				if(files.firstEntry().getValue().support.getRuntime() == null) {
					Message msg = new Message("This language has no runtime support!");
					msg.setTitle("Error");
					curr.openDialog(msg);
				}else {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream stream = new PrintStream(baos);
					String error = files.firstEntry().getValue().support.getRuntime().exe(curr, stream, files);
					if(error != null) {
						Message msg = new Message(error);
						msg.setTitle("Error");
						curr.openDialog(msg);
					}
				}
				
				return true;
			}
		});
		openDialog(file);
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active,
			float partialTicks) {
		if(onRender != null) onRender.run();
		
		super.render(laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
	}
	
	public void setOnRender(Runnable runnable) {
		this.onRender = runnable;
	}
	
	@Override
	public void load(NBTTagCompound tagCompound) {
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
		
	}
	
	public void addDynamicComponent(Component component) {
		super.getCurrentLayout().addComponent(component);
		markForLayoutUpdate();
	}
	
}

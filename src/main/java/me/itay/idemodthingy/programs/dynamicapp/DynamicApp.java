package me.itay.idemodthingy.programs.dynamicapp;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.Laptop;

import me.itay.idemodthingy.programs.bluej.Project;
import me.itay.idemodthingy.programs.bluej.api.comp.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class DynamicApp extends Application {
	
	private Runnable onRender, onTick;
	private Project currentProject;
	private boolean addedComponents;
	
	@Override
	public boolean handleFile(File file) {
		currentProject = Project.dearchive(file.getData());
		System.out.println(currentProject);
		return true;
	}
	
	@Override
	public void init() {
		
	}
	
	public void setOnRender(Runnable onRender) {
		this.onRender = onRender;
	}
	
	public void setOnTick(Runnable onTick) {
		this.onTick = onTick;
	}
	
	@Override
	public void onTick() {
		if(onTick != null) onTick.run();
		
		if(addedComponents) {
			addedComponents = false;
			markForLayoutUpdate();
		}
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active,
			float partialTicks) {
		if(onRender != null) onRender.run();
		
		super.render(laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
	}
	
	@Override
	public void load(NBTTagCompound arg0) {
		
	}

	@Override
	public void save(NBTTagCompound arg0) {
		
	}
	
	public void addComponent(Component component) {
		addComponent(component);
		addedComponents = true;
	}
	
	public Project getCurrentProject() {
		return currentProject;
	}
	
}

package com.example.examplemod;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.component.TextArea;

import net.minecraft.nbt.NBTTagCompound;

public class IDE extends Application {

	private IDETextArea text;
	
	private static final int WIDTH 	= 360;
	private static final int HEIGHT 	= 160;
	
	private static final int BOUNDS_SIZE = 5;
	private static final int BUTTONS_HEIGHT = 15;
	
	@Override
	public void init() {
		setDefaultWidth(360);
		setDefaultHeight(160);
		
		text = new IDETextArea(BOUNDS_SIZE, BOUNDS_SIZE * 2 + BUTTONS_HEIGHT, WIDTH - (BOUNDS_SIZE * 2), HEIGHT - (BOUNDS_SIZE * 3 + BUTTONS_HEIGHT), new JSLanguage());
		
		addComponent(text);
	}

	@Override
	public void load(NBTTagCompound tagCompound) {
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
		
	}

}

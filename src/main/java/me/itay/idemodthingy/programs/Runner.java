package me.itay.idemodthingy.programs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.ResponseHandler;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.Laptop;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class Runner extends Application {
	
	private String code;
	private IDELanguageSupport support;
	private Runnable onRender;
	
	@Override
	public void init() {
		Runner runner = this;
		
		OpenFile file = new OpenFile(this);
		file.setResponseHandler(new ResponseHandler<File>() {
			@Override
			public boolean onResponse(boolean success, File e) {
				NBTTagCompound data = e.getData();
				System.out.println(data);
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

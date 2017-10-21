package me.itay.idemodthingy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog.Message;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.ResponseHandler;
import com.mrcrayfish.device.api.io.File;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import net.minecraft.nbt.NBTTagCompound;

public class Runner extends Application {
	
	private String code;
	private IDELanguageSupport support;
	
	@Override
	public void init() {
		Runner runner = this;
		
		OpenFile file = new OpenFile(this);
		file.setResponseHandler(new ResponseHandler<File>() {
			@Override
			public boolean onResponse(boolean success, File e) {
				NBTTagCompound data = e.getData();
				if(data.hasKey("code")) {							
					code = data.getString("code");
				}
				if(data.hasKey("lang")) {
					String langName = data.getString("lang");
					IDELanguageSupport lang = IDELanguageManager.getSupport().get(langName);
					if(lang == null) {
						Message msg = new Message("Unknown Language");
						msg.setTitle("Error");
						openDialog(msg);
					}else {
						support = lang;

						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						PrintStream stream = new PrintStream(baos);
						String error = support.getRuntime().exe(runner, stream, code);
						if(error != null) {
							Message msg = new Message(error);
							msg.setTitle("Error");
							openDialog(msg);
						}
						String output = baos.toString();
						if(output.trim().isEmpty()) {
							output = "Run program succesfully!";
						}
						output = output.replaceAll("\r", "");
						Message msg = new Message(output);
						msg.setTitle("Output");
						openDialog(msg);					
					}
				}
				return true;
			}
		});
		openDialog(file);
	}

	@Override
	public void load(NBTTagCompound tagCompound) {
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
		
	}
	
	public void addDynamicComponent(Component component) {
		super.getCurrentLayout().addComponent(component);
	}
	
}

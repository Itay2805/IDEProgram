package me.itay.idemodthingy;

import java.util.function.Predicate;

import org.lwjgl.input.Mouse;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.ResponseHandler;
import com.mrcrayfish.device.api.app.Dialog.SaveFile;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.io.File;

import net.minecraft.nbt.NBTTagCompound;

public class IDE extends Application {

	private Button language;
	private Button run;
	private Button save;
	private Button load;
	private IDETextArea text;
	
	private static final int WIDTH 	= 360;
	private static final int HEIGHT 	= 160;
	
	private static final int BOUNDS_SIZE = 5;
	private static final int BUTTONS_HEIGHT = 15;
	
	@Override
	public void init() {
		setDefaultWidth(360);
		setDefaultHeight(160);
		
		language = new Button("Lang", BOUNDS_SIZE, BOUNDS_SIZE, BUTTONS_HEIGHT * 2, BUTTONS_HEIGHT);
		run = new Button("Run", BUTTONS_HEIGHT * 2 + BOUNDS_SIZE * 2, BOUNDS_SIZE, BUTTONS_HEIGHT * 2, BUTTONS_HEIGHT);
		save = new Button("Save", BUTTONS_HEIGHT * 4 + BOUNDS_SIZE * 3, BOUNDS_SIZE, BUTTONS_HEIGHT * 2, BUTTONS_HEIGHT);
		load = new Button("Load", BUTTONS_HEIGHT * 6 + BOUNDS_SIZE * 4, BOUNDS_SIZE, BUTTONS_HEIGHT * 2, BUTTONS_HEIGHT);
		text = new IDETextArea(BOUNDS_SIZE, BOUNDS_SIZE * 2 + BUTTONS_HEIGHT, WIDTH - (BOUNDS_SIZE * 2), HEIGHT - (BOUNDS_SIZE * 3 + BUTTONS_HEIGHT), new JSLanguage());
		
		addComponent(load);
		addComponent(save);
		addComponent(run);
		addComponent(language);
		addComponent(text);
		
		Application curr = this;
		
		load.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				OpenFile file = new OpenFile(curr);
				file.setResponseHandler(new ResponseHandler<File>() {
					@Override
					public boolean onResponse(boolean success, File e) {
						NBTTagCompound data = e.getData();
						if(data.hasKey("code")) {							
							String code = data.getString("code");
							text.setText(code);
						}
						return true;
					}
				});
				curr.openDialog(file);
			}
		});
		
		save.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				NBTTagCompound data = new NBTTagCompound();
				data.setString("code", text.getText());
				SaveFile file = new SaveFile(curr, new File("", curr, data));
				file.setResponseHandler(new ResponseHandler<File>() {
					@Override
					public boolean onResponse(boolean success, File e) {
						return true;
					}
				});
				curr.openDialog(file);
			}
		});
	}

	@Override
	public void load(NBTTagCompound tagCompound) {
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
		
	}

}

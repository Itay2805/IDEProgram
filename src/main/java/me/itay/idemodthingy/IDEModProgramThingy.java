package me.itay.idemodthingy;

import java.util.TreeMap;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.programs.ApplicationPixelPainter;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import me.itay.idemodthingy.languages.js.IDELanguageJavaScript;
import me.itay.idemodthingy.languages.js.IDELanguageRuntimeJS;
import me.itay.idemodthingy.languages.text.IDELanguageText;
import me.itay.idemodthingy.programs.IDE;
import me.itay.idemodthingy.programs.OpenGLTest;
import me.itay.idemodthingy.programs.Runner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = IDEModProgramThingy.MODID, version = IDEModProgramThingy.VERSION)
public class IDEModProgramThingy
{
    public static final String MODID = "idemodthingy";
    public static final String VERSION = "0.4.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	//load pixel painter
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), ApplicationPixelPainter.class);
		
    	// load the JSVM
		IDELanguageRuntimeJS temp = new IDELanguageRuntimeJS();
    	temp.exe(null, null, new TreeMap<>());
    	temp = null;
    	
    	IDELanguageSupport text = new IDELanguageSupport("Text", new IDELanguageText(), null);
    	IDELanguageManager.addSupport("Text", text);
    	
    	IDELanguageSupport js = new IDELanguageSupport("JavaScript", new IDELanguageJavaScript(), new IDELanguageRuntimeJS());
    	IDELanguageManager.addSupport("JavaScript", js);

    	ApplicationManager.registerApplication(new ResourceLocation("idemodthingy:ide"), IDE.class);
    	ApplicationManager.registerApplication(new ResourceLocation("idemodthingy:runtime"), Runner.class);
    	ApplicationManager.registerApplication(new ResourceLocation("idemodthingy:glcontexttest"), OpenGLTest.class);
    }
}

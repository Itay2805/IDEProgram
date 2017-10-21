package me.itay.idemodthingy;

import com.mrcrayfish.device.api.ApplicationManager;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import me.itay.idemodthingy.languages.js.IDELanguageJavaScript;
import me.itay.idemodthingy.languages.js.IDELanguageRuntimeJS;
import me.itay.idemodthingy.languages.text.IDELanguageText;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = IDEModProgramThingy.MODID, version = IDEModProgramThingy.VERSION)
public class IDEModProgramThingy
{
    public static final String MODID = "idemodthingy";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	IDELanguageSupport text = new IDELanguageSupport("text", new IDELanguageText(), null);
    	IDELanguageManager.addSupport("txt", text);
    	IDELanguageManager.addSupport("text", text);
    	
    	IDELanguageSupport js = new IDELanguageSupport("js", new IDELanguageJavaScript(), new IDELanguageRuntimeJS());
    	IDELanguageManager.addSupport("js", js);
    	IDELanguageManager.addSupport("javascript", js);

    	ApplicationManager.registerApplication(new ResourceLocation("idemodthingy:ide"), IDE.class);
    	ApplicationManager.registerApplication(new ResourceLocation("idemodthingy:runtime"), Runner.class);
    }
}

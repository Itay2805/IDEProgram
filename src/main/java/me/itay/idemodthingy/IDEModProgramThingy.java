package me.itay.idemodthingy;

import com.mrcrayfish.device.api.ApplicationManager;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
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
    	IDELanguageManager.addSupport("Text", new IDELanguageSupport(new IDELanguageText()));
    	
    	ApplicationManager.registerApplication(new ResourceLocation("idemodthingy:idethingy"), IDE.class);
    }
}

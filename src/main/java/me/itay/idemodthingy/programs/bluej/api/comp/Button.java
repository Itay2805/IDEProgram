package me.itay.idemodthingy.programs.bluej.api.comp;

import com.mrcrayfish.device.api.app.Icon;
import me.itay.idemodthingy.programs.bluej.resources.BlueJResourceLocation;
import net.minecraft.util.ResourceLocation;

public class Button extends com.mrcrayfish.device.api.app.component.Button implements Component{

    private String name;
    private BlueJResourceLocation resloc;

    public Button(int left, int top, String text, String name, BlueJResourceLocation resloc) {
        super(left, top, text);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, int buttonWidth, int buttonHeight, String text, String name, BlueJResourceLocation resloc) {
        super(left, top, buttonWidth, buttonHeight, text);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, Icon icon, String name, BlueJResourceLocation resloc) {
        super(left, top, icon);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, int buttonWidth, int buttonHeight, Icon icon, String name, BlueJResourceLocation resloc) {
        super(left, top, buttonWidth, buttonHeight, icon);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, String text, Icon icon, String name, BlueJResourceLocation resloc) {
        super(left, top, text, icon);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, int buttonWidth, int buttonHeight, String text, Icon icon, String name, BlueJResourceLocation resloc) {
        super(left, top, buttonWidth, buttonHeight, text, icon);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight, String name, BlueJResourceLocation resloc) {
        super(left, top, iconResource, iconU, iconV, iconWidth, iconHeight);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, int buttonWidth, int buttonHeight, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight, String name, BlueJResourceLocation resloc) {
        super(left, top, buttonWidth, buttonHeight, iconResource, iconU, iconV, iconWidth, iconHeight);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, String text, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight, String name, BlueJResourceLocation resloc) {
        super(left, top, text, iconResource, iconU, iconV, iconWidth, iconHeight);
        this.name = name;
        this.resloc = resloc;
    }

    public Button(int left, int top, int buttonWidth, int buttonHeight, String text, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight, String name, BlueJResourceLocation resloc) {
        super(left, top, buttonWidth, buttonHeight, text, iconResource, iconU, iconV, iconWidth, iconHeight);
        this.name = name;
        this.resloc = resloc;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public BlueJResourceLocation getResourceLocation(){
        return this.resloc;
    }
}

package me.itay.idemodthingy.programs.bluej.api;

import me.itay.idemodthingy.programs.bluej.Project;
import me.itay.idemodthingy.programs.bluej.api.comp.Button;
import me.itay.idemodthingy.programs.bluej.api.comp.ComboBox;
import me.itay.idemodthingy.programs.bluej.api.comp.Component;
import me.itay.idemodthingy.programs.bluej.resources.BlueJResourceLocation;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ComponentBuilder {
    private String text, name;
    private Integer x, y, width, wwidth, height, ix, iy, iwidth, iheight;
    private BlueJResourceLocation icon;
    private Component parent;
    private String type;
    private List<String> additionalParams = new ArrayList<>();

    private Project context;
    
    public ComponentBuilder(Project context){
        this.context = context;
    }
    
    public ComponentBuilder setText(String text){
        this.text = text;
        return this;
    }

    public ComponentBuilder setName(String name){
        this.name = name;
        return this;
    }
    
    public ComponentBuilder setLocation(int x, int y){
        this.x = x;
        this.y = y;
        return this;
    }

    public ComponentBuilder setSize(int width, int height){
        this.width = width;
        this.height = height;
        return this;
    }
    
    public ComponentBuilder setIcon(BlueJResourceLocation icon){
        this.icon = icon;
        return this;
    }

    public ComponentBuilder setIconPos(int ix, int iy){
        this.ix = ix;
        this.iy = iy;
        return this;
    }

    public ComponentBuilder setIconSize(int iwidth, int iheight){
        this.iwidth = iwidth;
        this.iheight = iheight;
        return this;
    }
    
    public ComponentBuilder setParent(Component parent){
        this.parent = parent;
        return this;
    }

    public ComponentBuilder setType(String type){
        this.type = type;
        return this;
    }

    public ComponentBuilder addExtraParameters(String...params){
        if(params.length != 0){
            additionalParams.addAll(Arrays.asList(params));
        }
        return this;
    }
    
    public Component build(){
        if(this.text != null
                && this.x != null
                && this.y != null
                && this.width != null
                && this.iwidth != null
                && iheight != null
                && icon != null
                && type != null
            ){
            if(type.toLowerCase().equals("button")){
                if(this.height != null && this.ix != null && this.iy != null && this.iwidth != null && this.iheight != null){
                    if(this.parent != null){
                        return new Button(
                                this.x,
                                this.y,
                                this.width,
                                this.height,
                                new ResourceLocation(
                                        this.icon.getDomain(),
                                        this.parent.getName() +
                                                "/" +
                                                this.icon.getPath()),
                                this.ix,
                                this.iy,
                                this.iwidth,
                                this.iheight,
                                this.name,
                                new BlueJResourceLocation("button", this.context.toString(), this.name));
                    }else{
                        return new Button(
                                this.x,
                                this.y,
                                this.width,
                                this.height,
                                new ResourceLocation(
                                        this.icon.getDomain(),
                                        this.icon.getPath()),
                                this.ix,
                                this.iy,
                                this.iwidth,
                                this.iheight,
                                this.name,
                                new BlueJResourceLocation("button", context.toString(), this.name));
                    }
                }
            }else if(type.toLowerCase().equals("comboboxlist")){
                if(this.height != null && this.ix != null && this.iy != null && this.iwidth != null && this.iheight != null){
                    if(this.parent != null){
                        return new ComboBox.List<>(
                                this.x,
                                this.y,
                                this.width,
                                this.width,
                                (String[])this.additionalParams.toArray(),
                                this.name,
                                new BlueJResourceLocation("comboboxlist", context.toString(), this.name)
                        );
                    }else{
                        return new ComboBox.List<>(
                                this.x,
                                this.y,
                                this.width,
                                this.width,
                                (String[])this.additionalParams.toArray(),
                                this.name,
                                new BlueJResourceLocation("comboboxlist", context.toString(), this.name)
                        );
                    }
                }
            }
        }
        return null;
    }
}

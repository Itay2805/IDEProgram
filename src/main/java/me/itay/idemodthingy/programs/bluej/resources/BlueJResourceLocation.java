package me.itay.idemodthingy.programs.bluej.resources;

public class BlueJResourceLocation {
    
	private String domain, context, path;
    private BlueJResourceLocation reslocCtx;

    public BlueJResourceLocation(String domain, String ctx, String path) {
        this.domain = domain;
        this.context = ctx;
        this.path = path;
    }
    
    public BlueJResourceLocation(String domain, BlueJResourceLocation ctx, String path) {
        this.domain = domain;
        this.context = "[" + ctx.toString() + "]";
        this.path = path;
        this.reslocCtx = ctx;
    }

    public BlueJResolvedResloc resolve() {
    	return BlueJReslocResolver.resolveBlueJResourceLocation(this);
    }
    
    @Override
    public String toString() {
        return this.domain + "://" + this.context + "/" + this.path;
    }

    public String getDomain(){
        return this.domain;
    }

    public String getContext(){
        return this.context;
    }
    
    public BlueJResourceLocation getReslocCtx() {
		return reslocCtx;
	}

    public String getPath(){
        return this.path;
    }
}

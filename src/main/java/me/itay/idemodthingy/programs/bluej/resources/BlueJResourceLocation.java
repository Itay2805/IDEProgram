package me.itay.idemodthingy.programs.bluej.resources;

public class BlueJResourceLocation {
    private String domain, context, path;

    public BlueJResourceLocation(String domain, String ctx, String path) {
        this.domain = domain;
        this.context = ctx;
        this.path = path;
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

    public String getPath(){
        return this.path;
    }
}

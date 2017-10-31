package me.itay.idemodthingy.programs.bluej.resources;

import me.itay.idemodthingy.programs.bluej.resources.types.BlueJFileSystemResloc;

public class BlueJReslocResolver {
	
	public static BlueJResolvedResloc resolveBlueJResourceLocation(BlueJResourceLocation resloc) {
		String domain = resloc.getDomain();
		switch(domain) {
		case "files":
			switch(resloc.getContext()) {
			case "root":
			default:
				return new BlueJFileSystemResloc(resloc.getPath());
			}
		case "project":
			return resolveProjectResloc(resloc);
		default:
			return null;
		}
	}
	
	private static BlueJResolvedResloc resolveProjectResloc(BlueJResourceLocation resloc) {
		if(resloc.getContext().startsWith("[")) {
			return resolveBlueJResourceLocation(resloc.getReslocCtx());
		}
		return null;
	}
	
}

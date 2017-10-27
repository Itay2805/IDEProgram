package me.itay.idemodthingy.programs.bluej;

import me.itay.idemodthingy.api.IDELanguageManager;
import me.itay.idemodthingy.api.IDELanguageSupport;
import net.minecraft.nbt.NBTTagCompound;

public class ProjectFile {
	
	private static final String FILENAME_TAG = "name";
	private static final String CODE_TAG = "code";
	private static final String LANGUAGE_TAG = "lang";
	
	private String name;
	private String code;
	private IDELanguageSupport language;
	
	public ProjectFile(String name, String code, IDELanguageSupport language) {
		this.name = name;
		this.code = code;
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public IDELanguageSupport getLanguage() {
		return language;
	}
	
	public void setLanguage(IDELanguageSupport language) {
		this.language = language;
	}
	
	@Override
	public String toString() {
		return "ProjectFile [name=" + name + ", code=" + code + ", language=" + language + "]";
	}
	
	public static ProjectFile fromNBT(NBTTagCompound data) {
		String name = data.getString(FILENAME_TAG);
		String code = data.getString(CODE_TAG);
		String lang = data.getString(LANGUAGE_TAG);
		IDELanguageSupport language = IDELanguageManager.getSupport().get(lang);
		return new ProjectFile(name, code, language);
	}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound file = new NBTTagCompound();
		file.setString(FILENAME_TAG, name);
		file.setString(CODE_TAG, code);
		file.setString(LANGUAGE_TAG, language.getName());
		return file;
	}
	
}

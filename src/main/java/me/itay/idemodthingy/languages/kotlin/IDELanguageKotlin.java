package me.itay.idemodthingy.languages.kotlin;

import com.mrcrayfish.device.api.app.Application;
import me.itay.idemodthingy.IDEModProgramThingy;
import me.itay.idemodthingy.api.IDELanguageHighlight;
import me.itay.idemodthingy.api.IDELanguageRuntime;
import me.itay.idemodthingy.components.IDETextArea;
import me.itay.idemodthingy.programs.IDE;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import java.util.regex.Pattern;

public class IDELanguageKotlin implements IDELanguageRuntime, IDELanguageHighlight{

    private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    private static final String[] DELIMITERS = { "\\s", "\\p{Punct}", "\\p{Digit}+"  };

    private static final String DELIMITER;
    static {
        StringJoiner joiner = new StringJoiner("|");
        for(String delim : DELIMITERS) {
            joiner.add(delim);
        }
        DELIMITER = joiner.toString();
    }

    //TODO: Do not execute kotlin, even though it will be disabled. Don't do anything with it! Will get this working!
    @Override
    public String exe(Application app, PrintStream out, TreeMap<String, IDE.ProjectFile> code) {
        try{
            ResourceLocation kotlindirresloc = new ResourceLocation(IDEModProgramThingy.MODID, "kotlin");
            ResourceLocation kotlinbinresloc = new ResourceLocation(IDEModProgramThingy.MODID, "kotlin/libexec/bin/");
            ResourceLocation outresloc = new ResourceLocation(IDEModProgramThingy.MODID, "kotlin/out/");
            File kotlindir = new File(kotlindirresloc.getResourcePath());
            File outdir = null;
            File kotlincfile = null;
            File outfile = null;
            if(!kotlindir.exists()){
                if(kotlindir.mkdir()){
                    outdir = new File(outresloc.getResourcePath());
                    kotlincfile = new File(String.format("%s/kotlinc-jvm", kotlinbinresloc.getResourcePath()));
                    outfile = new File(String.format("%s/output.jar", outresloc.getResourcePath()));

                }
            }else{
                outdir = new File(outresloc.getResourcePath());
                kotlincfile = new File(String.format("%s/kotlinc-jvm", kotlinbinresloc.getResourcePath()));
                outfile = new File(String.format("%s/output.jar", outresloc.getResourcePath()));
            }
            List<String> filePaths = new ArrayList<>();
            if(outdir == null) return "";
            if(outdir.exists()){
                if(outdir.isDirectory()){
                    writeAndCompile(filePaths, code);
                }else{
                    if(outdir.mkdir()){
                        writeAndCompile(filePaths, code);
                    }else{
                        throw new IOException(String.format("Cannot create directory %s. Don't know why. Talk to Alex about it.", outdir.getAbsolutePath()));
                    }
                }
            }else if(outdir.mkdir()){
                writeAndCompile(filePaths, code);
            }else{
                throw new IOException(String.format("Cannot create directory %s. Don't know why. Talk to Alex about it.", outdir.getAbsolutePath()));
            }

            List<String> commands = new ArrayList<>();
            {
                if(kotlincfile.exists()){
                    if(kotlincfile.isFile()){
                        commands.add(kotlincfile.getAbsolutePath());
                        commands.addAll(filePaths);
                        commands.add("-d");
                        commands.add(outfile.getAbsolutePath());

                    }
                }
            }
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process p = pb.start();
            return String.format("Success with exit code %d", p.exitValue());
        }catch(Exception e){
            String message = "";
            message+="Build failed with the following errors:\n"
                    +e.getMessage()
                    +"\n"
                    +"See console for more information.";
            System.err.println(message);
            e.printStackTrace();
            return message;
        }
    }

    private void writeAndCompile(List<String> filePaths, TreeMap<String, IDE.ProjectFile> code) throws IOException{
        for(IDE.ProjectFile projectFile : code.values()) {
            File file = new File(String.format("%s/%s.kt", new ResourceLocation(IDEModProgramThingy.MODID, "kotlin").getResourcePath(), projectFile.fileName));
            System.out.println(file.getAbsolutePath());
            if(!file.exists()) {
                if(file.createNewFile()) {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    code.forEach((s, pf) -> {
                        if (pf == projectFile) {
                            try {
                                bw.write(s);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally{
                                try {
                                    bw.flush();
                                    bw.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }else{
                throw new FileAlreadyExistsException(
                        String.format(
                                "%s already exists. This should not be happening like at all! " +
                                        "If this is happening, tell Alex to strap up!",
                                file.getAbsolutePath()));
            }
            filePaths.add(file.getAbsolutePath());
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public String[] tokenize(String text) {
        return text.split(String.format(WITH_DELIMITER, "(" + DELIMITER + ")"));
    }

    private List<String> previousTokens = new ArrayList<>();
    private String str = Pattern.compile("[a-zA-Z]+").pattern();
    @Override
    public int getKeywordColor(String text) {
        int color = 0xffffff;
        switch(text){
            case "fun":
                color = 0x0000A4;
                break;
            case "class":
                color = 0x8f0000;
                break;
            case "interface":
                color = 0xF836FF;
                break;
            case "inline":
                color = 0x36CDF;
                break;
            case "infix":
                color = 0x36CDF;
                break;
            case "override":
                color = 0x0C00EB;
                break;
            case "object":
                color = 0x0C00EB;
                break;
            case "operator":
                color = 0x0C00EB;
                break;
            case "var":
                color = 0x0C00EB;
                break;
            case "val":
                color = 0x0C00EB;
                break;
            case "const":
                color = 0x948FFF;
                break;
            case "final":
                color = 0x948FFF;
                break;
            case "public":
                color = 0x948FFF;
                break;
            case "protected":
                color = 0x948FFF;
                break;
            case "private":
                color = 0x948FFF;
                break;
            case "package":
                color = 0x948FFF;
                break;
            case "as":
                color = 0x1FB800;
                break;
            case "break":
                color = 0xFF005D;
                break;
            case "continue":
                color = 0xFF005D;
                break;
            case "do":
                color = 0x948FFF;
                break;
            case "while":
                color = 0x948FFF;
                break;
            case "if":
                color = 0x948FFF;
                break;
            case "else":
                color = 0x948FFF;
                break;
            case "for":
                color = 0x948FFF;
                break;
            case "false":
                color = 0x948FFF;
                break;
            case "true":
                color = 0x948FFF;
                break;
            case "in":
                color = 0xDE00BD;
                break;
            case "is":
                color = 0xDE00BD;
                break;
            case "null":
                color = 0x948FFF;
                break;
            case "return":
                color = 0xFF005D;
                break;
            case "super":
                color = 0x948FFF;
                break;
            case "this":
                color = 0x980;
                break;
            case "throw":
                color = 0x948FFF;
                break;
            case "try":
                color = 0x948FFF;
                break;
            case "typealias":
                color = 0x948FFF;
                break;
            case "when":
                color = 0x948FFF;
                break;
            case "?":
                switch(previousTokens.get(1)){
                    case "as":
                        color = 0x1FB8A0;
                        break;
                    case "is":
                        color = 0xDE00B5;
                        break;
                }
            default:
                if(!previousTokens.isEmpty()) {
                    if(previousTokens.size() == 2 || previousTokens.size() == 1) {
                        switch (previousTokens.get(0)) {
                            case "fun":
                                color = 0xFFE08C;
                                break;
                            case "class":
                                color = 0xCF7DFF;
                                break;
                            case "interface":
                                color = 0xCF7DFF;
                                break;
                            case "inline":
                                color = 0xFFE080;
                                break;
                            case "infix":
                                color = 0xFFE05F;
                                break;
                            case "object":
                                color = 0xED5353;
                                break;
                            case "var":
                                color = 0xB6BCFA;
                                break;
                            case "val":
                                color = 0xB6BCF0;
                                break;
                            case "package":
                                color = 0xFAE9B6;
                                break;
                            case "is":
                                color = 0xFAE9B6;
                                break;
                            case "return":
                                color = 0xFAE9B6;
                                break;
                            case "typealias":
                                color = 0xFAE9B6;
                                break;
                        }
                    }
                    if(previousTokens.size() == 2) {
                        switch (previousTokens.get(1)) {
                            case "override":
                                color = 0xFFE080;
                                break;
                            case "operator":
                                color = 0xFFE080;
                                break;
                        }
                    }
                }
        }
        if(previousTokens.size() == 2) {
            Iterator<String> it = previousTokens.iterator();
            previousTokens.clear();
            while(it.hasNext()){
                previousTokens.add(it.next());
                previousTokens.add(text);
            }
        }else{
            previousTokens.add(text);
        }
        return color;
    }

    @Override
    public void errorCheck(IDETextArea area, String code) {

    }
}

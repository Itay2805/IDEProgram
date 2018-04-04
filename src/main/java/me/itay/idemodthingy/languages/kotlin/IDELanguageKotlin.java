package me.itay.idemodthingy.languages.kotlin;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.TreeMap;

import com.mrcrayfish.device.api.app.Application;

import me.itay.idemodthingy.api.IDELanguageRuntime;
import me.itay.idemodthingy.programs.bluej.Project;
import me.itay.idemodthingy.programs.bluej.ProjectFile;
import me.itay.idemodthingy.programs.bluej.api.Problem;
import me.itay.idemodthingy.programs.bluej.api.SyntaxHighlighter;
import me.itay.idemodthingy.programs.bluej.api.tokens.Token;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class IDELanguageKotlin implements IDELanguageRuntime, SyntaxHighlighter {

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

    @Override
    public String exe(Application app, PrintStream out, TreeMap<String, ProjectFile> code) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("kotlin");
        if(engine != null){
            if(!code.isEmpty()) {
                try {
                    engine.eval(code.firstEntry().getValue().getCode());
                    return null;
                } catch (ScriptException e) {
                    return e.getMessage();
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Token> parse(Project project, ProjectFile currentFile) {
        return null;
    }

    @Override
    public List<Problem> getProblems(ProjectFile file) {
        return null;
    }

    @Override
    public void reset() {

    }

    public String[] tokenize(String text) {
        return text.split(String.format(WITH_DELIMITER, "(" + DELIMITER + ")"));
    }

    private List<String> previousTokens = new ArrayList<>();
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
}

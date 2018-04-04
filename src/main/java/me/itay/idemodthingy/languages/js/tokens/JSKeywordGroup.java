package me.itay.idemodthingy.languages.js.tokens;

import me.itay.idemodthingy.programs.bluej.api.tokens.Token;
import me.itay.idemodthingy.programs.bluej.api.tokens.TokenGroup;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class JSKeywordGroup implements Token {

    private static String[] tokens = {
            "function",
            "break",
            "do",
            "case",
            "else",
            "catch",
            "finally",
            "return",
            "continue",
            "switch",
            "default",
            "if",
            "throw",
            "try",
            "for",
            "this",
            "var",
            "instanceof",
            "typeof",
            "debugger",
            "function",
            "delete",
            "in",
            "null",
            "true",
            "false",
            "void",
            "new"
    };

    @Override
    public int getColor() {
        return Color.BLUE.getRGB();
    }

    public static List<String> getTokens() {
        return Arrays.asList(tokens);
    }
}

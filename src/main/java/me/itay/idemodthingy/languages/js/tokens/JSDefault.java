package me.itay.idemodthingy.languages.js.tokens;

import me.itay.idemodthingy.programs.bluej.api.tokens.Token;

import java.awt.*;

public class JSDefault implements Token{

    @Override
    public int getColor() {
        return Color.WHITE.getRGB();
    }
}

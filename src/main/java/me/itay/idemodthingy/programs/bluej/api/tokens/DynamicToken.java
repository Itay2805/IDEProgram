package me.itay.idemodthingy.programs.bluej.api.tokens;

import java.util.regex.Pattern;

public class DynamicToken implements Token {
    private String token;

    public DynamicToken(String token){
        this.token = token;
    }

    public String getToken(){
        return this.token;
    }

    @Override
    public int getColor() {
        return 0;
    }
}

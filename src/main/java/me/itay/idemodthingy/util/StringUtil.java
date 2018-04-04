package me.itay.idemodthingy.util;

import me.itay.idemodthingy.programs.bluej.Project;

import java.io.InputStream;

public class StringUtil {
    public static String convertStreamToString(InputStream is) {
        @SuppressWarnings("resource")
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String getFromProjectString(byte toGet, String project){
        switch(toGet){
            case 0:
                return getSubstringByProperty(project, "name");
            case 1:
                return getSubstringByProperty(project, "path");
            case 2:
                return getSubstringByProperty(project, "resolved");
        }
        return "";
    }

    private static String getSubstringByProperty(String project, String property){
        int ni = project.indexOf(property + "=");
        String n = project.substring(ni);
        return n.substring(
                0,
                project.indexOf(',')
        );
    }
}

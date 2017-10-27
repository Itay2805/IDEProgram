package me.itay.idemodthingy.util;

import java.io.InputStream;

public class StringUtil {
    public static String convertStreamToString(InputStream is) {
        @SuppressWarnings("resource")
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

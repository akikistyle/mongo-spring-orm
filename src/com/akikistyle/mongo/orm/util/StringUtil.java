package com.akikistyle.mongo.orm.util;

public class StringUtil {

    public static String concat(String... strArray) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
        return sb.toString();
    }
}

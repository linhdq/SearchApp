package com.example.linhdq.searchapp.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by LinhDQ on 1/17/17.
 */

public class StringUtils {
    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }
}

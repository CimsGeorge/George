package edu.tongji.cims.kgt.util;

/**
 * @author Yue Lin
 * @version 0.0.1
 */
public class Tokenizer {

    public static String replaceUnderlineWithSpace(String s) {
        if (s.contains("_"))
            s = s.replace('_', ' ');
        return s;
    }
}

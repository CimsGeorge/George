package edu.tongji.cims.kgt.util;

/**
 * @author Yue Lin
 * @since 2018-12-25
 */
public class Tokenizer {

    public static String replaceUnderlineWithSpace(String s) {
        if (s.contains("_"))
            s = s.replace('_', ' ');
        return s;
    }
}

package io.renren.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Matching {
    public static String reg(String str,String reg) {

        String  strs=str;
        String regs=reg;
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        m.find();
        return  m.group(0);



    }
}

package cn.qaq.valveapi.utils;

import java.io.UnsupportedEncodingException;

public class StringUtil {

    public static String mapGet(Object str){
        if (null == str){
            return null;
        }
        return str.toString();
    }

    public static String tran(String weGroupId) {
        try {
            return new String(weGroupId.getBytes("ISO8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

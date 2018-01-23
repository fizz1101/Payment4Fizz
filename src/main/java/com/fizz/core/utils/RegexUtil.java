package com.fizz.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    /**
     * 获取url参数
     * @param url   url地址
     * @param name  参数名
     * @param index 0：参数名+值；2：值
     * @return
     */
    public static String getUrlParam(String url, String name, int index) {
        String res = "";
        String regEx = "(^|&)" + name + "=([^&]*)(&|$)";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            res = matcher.group(index);
        }
        return res;
    }

    public static String removeUrlParams(String url, String[] params) {
        String reg = null;
        for (int i = 0; i < params.length; i++) {
            reg = "(?<=[\\?&])" + params[i] + "=[^&]*&?";
            url = url.replaceAll(reg, "");
        }
        url = url.replaceAll("&+$", "");
        return url;
    }


}

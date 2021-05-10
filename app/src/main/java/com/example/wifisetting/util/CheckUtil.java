package com.example.wifisetting.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {

    //检测ip地址是否合法
    public static boolean checkIpStr(String str){
        if(str==null||str.isEmpty()){
            return false;
        }
        //正则表达式判断
        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";//限定输入格式
        Pattern p=  Pattern.compile(ip);
        Matcher m=p.matcher(str);
        return m.matches();
    }

}

package com.micx.apitest.apiframework.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析正则
 */
public class PatternUtil {


    /**
     * 获取正则表达式字符串
     * @param content
     * @return
     */
    public static String getPatternStr(String content){
        String regex = "\\$\\{(.*?)}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =  pattern.matcher(content);
        String result = "";
        while (matcher.find()){
//            System.out.println(matcher.group());
//            System.out.println(matcher.group(1));
//            result = matcher.group(1);
            result = matcher.group();
        }

        return result;
    }

    /**
     * 返回替换表达式后的字符串
     * @param content
     * @param replaceStr
     * @return
     */
    public static String getReplaceStr(String content,String replaceStr){
        String regex = "\\$\\{(.*?)}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =  pattern.matcher(content);
        String result = "";
        while (matcher.find()){
            result = matcher.group();
        }
        if(!StringUtils.isEmpty(result)) {
            content = content.replace(result, replaceStr);
        }
        return content;
    }

    public static boolean isHasPattern(String content) {
        String regex = "\\$\\{(.*?)}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        boolean flag = false;
        if(matcher.find()){
            flag = true;
        }
        return flag;
    }

    public static void main(String[] args) {
//        String content = "Basic ${base64}skdjflskjdlkj${md5}";
        String content = "Basic ${base64}";
//        String patternStr = getPatternStr(content);
//        System.out.println(patternStr);
//        String replace = content.replace(patternStr, "11");
//        System.out.println(replace);



        String str = getReplaceStr(content, "soso");
//        System.out.println(str);
//
//        System.out.println(isHasPattern(content));


        Map<String,Object> testMap = new HashMap<>();
//        testMap.put("name","${name}");
        testMap.put("age","11");
        testMap.put("weight","${weight}");

        System.out.println(isHasPattern(testMap.toString()));

    }



}

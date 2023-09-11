package com.micx.apitest.apiframework.utils;

import com.alibaba.fastjson2.JSONPath;

/**
 * 提取参数
 */
public class ExtractsUtil {


    public static Object extractByJson(String response,String jsonPath){
        return JSONPath.eval(response, jsonPath);
    }


}

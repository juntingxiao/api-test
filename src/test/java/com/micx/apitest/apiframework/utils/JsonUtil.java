package com.micx.apitest.apiframework.utils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;

import java.util.Map;

public class JsonUtil {

    /**
     * 通过jsonpath表达式替换参数
     * @param srcJson
     * @param parseJson
     * @return
     */
    public static JSONObject parseJson(JSONObject srcJson,JSONObject parseJson){
        for (Map.Entry<String,Object> map:parseJson.entrySet()){
            JSONPath.set(srcJson,"$."+map.getKey(),map.getValue());
        }
        return srcJson;
    }

    public static void main(String[] args) {
//        String srcJson="{\n" +
//                "  \"name\": \"monkey\",\n" +
//                "  \"city\": \"Beijing\",\n" +
//                "  \"channelTime\": \"201809\",\n" +
//                "  \"students\": [\n" +
//                "    {\n" +
//                "      \"name\": \"xiaobai\",\n" +
//                "      \"age\": 10,\n" +
//                "      \"city\": {\n" +
//                "        \"sheng\": \"hebei\",\n" +
//                "        \"shi\":\"tangshan\"\n" +
//                "      }\n" +
//                "}\n" +
//                "]      \n" +
//                "}";

//        String parseJson = "{\n" +
//                "    \"name\":\"banana\",\n" +
//                "    \"students[0].name\":\"xiaohua\"\n" +
//                "}";

        String srcJson = "{\n" +
                "  \"ossFileName\": {\n" +
                "    \"ossFileName\": \"11\"\n" +
                "  },\n" +
                "  \"workOrderId\": \"${workOrderId}\"\n" +
                "}";

        String parseJson = "{\n" +
                "    \"workOrderId\":\"soso11111\",\n" +
                "    \"ossFileName.ossFileName\":\"xiaohua\"\n" +
                "}";

        JSONObject resut = JsonUtil.parseJson(JSONObject.parseObject(srcJson), JSONObject.parseObject(parseJson));
        System.out.println(JSONObject.parseObject(parseJson));
        System.out.println(JSONObject.parseObject(srcJson));
        System.out.println(resut);

    }


}

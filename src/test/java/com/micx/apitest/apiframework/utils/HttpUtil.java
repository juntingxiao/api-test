package com.micx.apitest.apiframework.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.micx.apitest.apiframework.apimodel.ApiModel;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.util.Map;

public class HttpUtil {

    public static String sendPostForJson(String json,String url){
        String result = "";
        ExtractableResponse<Response> extract = RestAssured
                .given()
                .contentType("application/json")
                .body(json)
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .extract();

        return extract.response().asString();

    }

    public RequestSpecification getRequestSpecification(){
        RequestSpecification requestSpecification = RestAssured.given().log().all();
        return requestSpecification;
    }

    /**
     * 设置请求头部
     * @param requestSpecification
     * @param headers
     * @param actionParams
     */
    public void setHeader(RequestSpecification requestSpecification,Map<String,Object> headers,Map<String,Object> actionParams){
        if (null != headers) {
            String result = replaceParams(headers, actionParams);
            requestSpecification.headers(JSONObject.parseObject(result));
            Allure.addAttachment("请求头", JSON.toJSONString(result));
        }
    }

    /**
     * 设置表单请求数据
     * @param requestSpecification
     * @param formParams
     * @param actionParams
     */
    public void setFormParams(RequestSpecification requestSpecification,Map<String,Object> formParams,Map<String,Object> actionParams){
        //设置表单请求 替换形参
        if (null != formParams) {
            String result = replaceParams(formParams, actionParams);
            requestSpecification.formParams(JSONObject.parseObject(result));
            Allure.addAttachment("表单请求参数", JSON.toJSONString(result));
        }
    }

    /**
     * 设置请求查询参数
     * @param requestSpecification
     * @param queryParams
     * @param actionParams
     */
    public void setQueryParams(RequestSpecification requestSpecification,Map<String,Object> queryParams,Map<String,Object> actionParams){
        //设置查询参数
        if (null != queryParams) {
            String result = replaceParams(queryParams, actionParams);
            requestSpecification.queryParams(JSONObject.parseObject(result));
            Allure.addAttachment("查询请求参数", JSON.toJSONString(result));
        }
    }

    /**
     * 设置上传文件路径
     * @param requestSpecification
     * @param uploadFile
     * @param actionParams
     */
    public void setUploadFile(RequestSpecification requestSpecification,String uploadFile,Map<String,Object> actionParams){
        if (null != uploadFile) {
            String resolver_result = PlaceholderUtil.getDefaultResolver()
                    .resolveByMap(uploadFile, actionParams);
            requestSpecification.multiPart(new File(resolver_result));
            Allure.addAttachment("文件上传请求参数", resolver_result);
        }
    }

    /**
     * 设置请求body json格式
     * @param requestSpecification
     * @param body
     * @param actionParams
     * @param jsonPathParams
     */
    public void setBody(RequestSpecification requestSpecification,
                        Map<String,Object> body,
                        Map<String,Object> actionParams,
                        Map<String,Object> jsonPathParams){
        //设置json请求
        if (null != body) {
            JSONObject jsonObject = null;
            //利用jsonpath表达式替换
            if (null != jsonPathParams) {
                jsonObject = JsonUtil.parseJson(
                        JSONObject.parseObject(JSON.toJSONString(body)),
                        JSONObject.parseObject(JSON.toJSONString(jsonPathParams)));
            }
            String json ="";
            if(null != jsonObject){
                json = replaceParams(jsonObject, actionParams);
                requestSpecification.body(json);
                Allure.addAttachment("json请求参数", json);
                return;
            }
            json = replaceParams(body, actionParams);
            requestSpecification.body(json);
            Allure.addAttachment("json请求参数", json);
        }
    }

    /**
     * 获取请求Url
     * 替换url的占位符
     * @param url
     * @param params
     * @return
     */
    public String getRequestUrl(String url,Map<String,Object> params){
        String new_url = PlaceholderUtil.getDefaultResolver().resolveByMap(url, params);
        return new_url;
    }


    /**
     * 处理参数替换
     *
     * @param params
     * @param actionParams
     * @return
     */
    private String replaceParams(Map<String, Object> params, Map<String, Object> actionParams) {
        //从实参替换参数
        String resolve = PlaceholderUtil.getDefaultResolver().resolveByMapReturnAsStr(JSON.toJSONString(params), actionParams);
        if (PatternUtil.isHasPattern(resolve)) {
            //从全局map替换参数
            resolve = PlaceholderUtil.getDefaultResolver().resolveByMapReturnAsStr(resolve, ThreadUtil.getTestCase().getGlobal_map());
        }
        return resolve;
    }


    public Response send(RequestSpecification requestSpecification,String method,String url){
//        RequestSpecification requestSpecification = RestAssured.given().log().all();
        return requestSpecification.request(method,url);
    }


}

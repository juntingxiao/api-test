package com.micx.apitest.apiframework.apimodel;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.micx.apitest.apiframework.utils.*;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class ApiModel {

    private String name;
    private String url;
    private String method;
    private Map<String, Object> headers;

    private String desc;

    //json请求
    private Map<String, Object> body;
    //表单请求
    private Map<String, Object> formParams;
    //查询条件
    private Map<String, Object> queryParams;

    //上传文件
    private String uploadFile;

    private Map<String, Object> variableParameter = new HashMap<>();

    public static ApiModel loadYaml(String path) {
        ApiModel apiModel = YamlUtil.readYamlByPath(path, ApiModel.class);
        return apiModel;
    }

//    apiModel执行的方法
    public Response run(Map<String, Object> actionParams, Map<String, Object> jsonPathParams) {
        HttpUtil httpUtil = new HttpUtil();
        RequestSpecification requestSpecification = httpUtil.getRequestSpecification();
        //判断头部是否存在需要替换的地方
        //设置头部 实参替换形参
        httpUtil.setHeader(requestSpecification,this.headers,actionParams);
        //设置表单请求 替换形参
        httpUtil.setFormParams(requestSpecification,this.formParams, actionParams);
        //设置json body请求
        httpUtil.setBody(requestSpecification,this.body,actionParams,jsonPathParams);
        //设置查询参数
        httpUtil.setQueryParams(requestSpecification,this.queryParams, actionParams);
        //设置上传文件路径
        httpUtil.setUploadFile(requestSpecification,this.uploadFile, actionParams);

        //获取请求url,包括替换url的占位符：${x}
        this.url = httpUtil.getRequestUrl(this.url, actionParams);
        this.url = httpUtil.getRequestUrl(this.url, ThreadUtil.getTestCase().getGlobal_map());
        String caseUrl = ThreadUtil.getTestCase().getEnvironment().getBaseUrl();
        String globalUrl =ConfigUtil.readConf("src/main/resources/conf.ini","servers","test");
        String baseUrl;
        if(!StringUtils.isEmpty(caseUrl)){
            baseUrl=caseUrl;
        }
        else {
            baseUrl=globalUrl;
        }
        this.getRequestUrl(baseUrl);

        Response response = httpUtil.send(requestSpecification, this.method, this.url);
        return response;
    }

    //    apiModel执行的方法
//    public Response run(Map<String, Object> actionParams, Map<String, Object> jsonPathParams) {
//        RequestSpecification requestSpecification = RestAssured.given().log().all();
//
//        //判断头部是否存在需要替换的地方
//        //设置头部 实参替换形参
//        if (null != this.headers) {
//            String result = replaceParams(this.headers, actionParams);
//            requestSpecification.headers(JSONObject.parseObject(result));
//            Allure.addAttachment("请求头", JSON.toJSONString(result));
//        }
//
//        //设置表单请求 替换形参
//        if (null != this.formParams) {
//            String result = replaceParams(this.formParams, actionParams);
//            requestSpecification.formParams(JSONObject.parseObject(result));
//            Allure.addAttachment("表单请求参数", JSON.toJSONString(result));
//        }
//
//        //设置json请求
//        if (null != this.body) {
//            JSONObject jsonObject = null;
//            //利用jsonpath表达式替换
//            if (null != jsonPathParams) {
//                jsonObject = JsonUtil.parseJson(
//                        JSONObject.parseObject(JSON.toJSONString(this.body)),
//                        JSONObject.parseObject(JSON.toJSONString(jsonPathParams)));
//            }
//            String json = replaceParams(jsonObject, actionParams);
//            requestSpecification.body(json);
//            Allure.addAttachment("json请求参数", json);
//        }
//
//        //设置查询参数
//        if (null != this.queryParams) {
//            String result = replaceParams(this.queryParams, actionParams);
//            requestSpecification.queryParams(JSONObject.parseObject(result));
//            Allure.addAttachment("查询请求参数", JSON.toJSONString(result));
//        }
//
//        if (null != this.uploadFile) {
//            String resolver_result = PlaceholderUtil.getDefaultResolver()
//                    .resolveByMap(this.uploadFile, actionParams);
//            requestSpecification.multiPart(new File(resolver_result));
//            Allure.addAttachment("文件上传请求参数", resolver_result);
//        }
//
//        //替换url的参数
//        url = PlaceholderUtil.getDefaultResolver().resolveByMap(this.url, actionParams);
//        url = PlaceholderUtil.getDefaultResolver().resolveByMap(url, ThreadUtil.getTestCase().getGlobal_map());
//
//        String baseUrl = ThreadUtil.getTestCase().getEnvironment().getBaseUrl();
//        this.getRequestUrl(baseUrl);
//        //发起请求
//        Response response = requestSpecification.request(this.method, this.url);
//        return response;
//    }

    public void getRequestUrl(String baseUrl){
        if(!StringUtils.isEmpty(baseUrl)) {
            if (this.url.contains("http")||this.url.contains("https")){
                log.info("请求url已经拼接过");
            }else {
                url = baseUrl + url;
            }
        }
    }


}

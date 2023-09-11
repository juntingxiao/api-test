package com.micx.apitest.apiframework.entity;

/**
 * 函数关键字枚举
 *
 */
public enum FunctionEnum {
    BASE64("base64"),
    RANDOMSTRING("randomString");

    private String functionName;

    FunctionEnum(String functionName){
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }
}

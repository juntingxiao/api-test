package com.micx.apitest.apiframework.entity;


public enum DataSourceEnum {
    TEST("test","测试环境数据源"),
    UAT("uat","预发布环境数据源");

    private String dataSourceName;
    private String desc;

    private DataSourceEnum(String dataSourceName,String desc){
        this.dataSourceName = dataSourceName;
        this.desc = desc;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public String getDesc() {
        return desc;
    }
}

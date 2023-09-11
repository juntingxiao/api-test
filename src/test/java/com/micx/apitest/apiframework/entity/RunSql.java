package com.micx.apitest.apiframework.entity;

import com.micx.apitest.apiframework.utils.DBUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class RunSql {
    //前置sql
    private String beforeSql;
    private List<Object> beforeSqlValue;
    //保存用例前置替换参数后的真实参数
    private List<Object> actualBeforeSqlValue = new ArrayList<>();

    //后置sql
    private String afterSql;
    private List<Object> afterSqlValue;
    //保存用例后置替换参数后的真实参数
    private List<Object> actualAfterSqlValue = new ArrayList<>();

    /**
     * 执行sql
     * @param env
     * @param executeSql
     * @param actualSqlValue
     * @return
     */
    public Map<String, Object> runSql(String env,String executeSql,List<Object> actualSqlValue) {

        Map<String, Object> selectResult = null;
        // select查询
        if (executeSql.contains("select")) {
            selectResult = DBUtil.selectOne(executeSql, env, actualSqlValue.toArray());
        }else if(executeSql.contains("update") || executeSql.contains("delete")
        ){
            DBUtil.updateData(executeSql, env, actualSqlValue.toArray());
        }
        return selectResult;
    }

    /**
     * 执行前置和后置的sql
     * @param executeSql
     * @param actualSqlValue
     * @return
     */
    public Map<String, Object> runBeforeAndAfterSql(String env,String executeSql,List<Object> actualSqlValue){
        Map<String, Object> selectResult = new HashMap<>();
        if(null != executeSql && DataSourceEnum.TEST.getDataSourceName().equalsIgnoreCase(env)){
            return this.runSql(DataSourceEnum.TEST.getDataSourceName(), executeSql, actualSqlValue);
        }else if(null != executeSql && DataSourceEnum.UAT.getDataSourceName().equalsIgnoreCase(env)){
            return this.runSql(DataSourceEnum.UAT.getDataSourceName(),executeSql,actualSqlValue);
        }
        return selectResult;
    }

}

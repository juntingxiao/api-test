package com.micx.apitest.apiframework.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据操作
 *
 */
@Slf4j
public class DBUtil {

    private final static DataSource dataSource = new ComboPooledDataSource();
    private final static DataSource uat_dataSource = new ComboPooledDataSource("uat");

    //保存数据源
    private static Map<String,DataSource> dataSourceMap = new ConcurrentHashMap<>();

    static {
        dataSourceMap.put("uat",uat_dataSource);
        dataSourceMap.put("test",dataSource);
    }

    public static DataSource getDataSource(String dataSourceName){
        if(StringUtils.isEmpty(dataSourceName)){
            throw new RuntimeException("获取数据源失败,请确认数据源名称："+dataSourceName);
        }
        return dataSourceMap.get(dataSourceName);
    }

    public static Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("获取数据库连接失败");
        }
    }

    public static void release(Connection connection, Statement statement, ResultSet resultSet){
        DbUtils.closeQuietly(connection,statement,resultSet);
    }

    public static void main(String[] args) throws SQLException {
//        Connection connection = DBUtil.getConnection();
//        System.out.println(connection);
        Object[] params = {1};
        String sql = "select * from mixc_cpms_workorder_uat.task_info limit ?";
//        selectOne(sql,"test",params);
        selectList(sql,"test",params);
    }

    /**
     * 查询
     * @param sql
     * @param dataSourceName
     * @param params
     * @return
     * @throws SQLException
     */
    public static Map<String,Object> selectOne(String sql,String dataSourceName,Object[] params){
        QueryRunner queryRunner = new QueryRunner(DBUtil.getDataSource(dataSourceName));
        Map<String,Object> result = null;
        try {
            result = queryRunner.query(sql, new MapHandler(),params);
        } catch (Exception e) {
            log.error("执行sql出现异常-sql:{},异常：{}",sql,e);
            throw new RuntimeException("执行sql出现异常");
        }
        log.info("执行的sql:{}",sql);
        log.info("执行的sql的参数是:{}", JSON.toJSONString(params));
        log.info("结果：{}",result);
        return result;
    }


    public static List<Map<String, Object>> selectList(String sql,String dataSourceName,Object[] params){
        QueryRunner queryRunner = new QueryRunner(DBUtil.getDataSource(dataSourceName));
        List<Map<String, Object>> result = null;
        try {
            result = queryRunner.query(sql, new MapListHandler(), params);
        } catch (Exception e) {
            log.error("执行sql出现异常-sql:{},异常：{}",sql,e);
            throw new RuntimeException("执行sql出现异常");
        }
        log.info("执行的sql:{}",sql);
        log.info("执行的sql的参数是:{}",params);
        log.info("结果：{}",result);
        return result;
    }

    /**
     * 更新sql
     * @param sql
     * @param dataSourceName
     * @param params
     */
    public static void updateData(String sql,String dataSourceName,Object[] params){
        QueryRunner queryRunner = new QueryRunner(DBUtil.getDataSource(dataSourceName));
        try {
            queryRunner.update(sql, params);
        } catch (Exception e) {
            log.error("执行sql出现异常-sql:{},异常：{}",sql,e);
            throw new RuntimeException("执行sql出现异常");
        }
    }



}

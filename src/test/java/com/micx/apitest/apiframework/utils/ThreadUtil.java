package com.micx.apitest.apiframework.utils;

import com.micx.apitest.apiframework.entity.TestCase;

/**
 * 线程工具类，解决并发的线程安全问题
 */
public class ThreadUtil {

    //线程隔离，保存每个TestCase
    public static final ThreadLocal<TestCase> global_map = new ThreadLocal<>();

    public static TestCase getTestCase(){
        return global_map.get();
    }

    public static void setTestCase(TestCase testCase){
        global_map.set(testCase);
    }


}

package com.micx.apitest.apiframework.cases;

import com.alibaba.fastjson2.JSONObject;
import com.micx.apitest.apiframework.utils.FunctionUtil;
import com.micx.apitest.apiframework.utils.HttpUtil;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.HashMap;
import java.util.Map;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

/**
 * 打包运行的同一入口
 */
@Slf4j
public class LauncherCase {

    private final static Boolean send_flag = false;
    private final static String run_work_web_hook_url = "https://open.rwork.crc.com.cn/open-apis/bot/v2/hook/b43d393f-e45f-4df3-9f54-45b4dbe85f2a";

    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectPackage("com.micx.apitest.apiframework")
//                        selectClass(MyTestClass.class)
                )
//                .filters(
//                        includeClassNamePatterns(".*Test")
//                )
                .build();
        //创建launcher
        Launcher launcher = LauncherFactory.create();
        //监听器
//        TestExecutionListener listener = new SummaryGeneratingListener();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        //通过监听器获取汇总数据
        TestExecutionSummary summary = listener.getSummary();
        log.info("用例总数：{}",summary.getTestsFoundCount());
        log.info("成功：{}条",summary.getTestsSucceededCount());
        log.info("失败：{}条",summary.getTestsFailedCount());

        //发送的内容模板
        String content ="{\"msg_type\":\"post\",\"content\":{\"post\":{\"zh_cn\":{\"title\":\"商业物业接口自动化\",\"content\":[[{\"tag\":\"text\",\"text\":\"时间：${date}\"}],[{\"tag\":\"text\",\"text\":\"总用例数：${total}\"}],[{\"tag\":\"text\",\"text\":\"成功：${success}\"}],[{\"tag\":\"text\",\"text\":\"失败：${fail}\"}]]}}}}\n";
        Map<String,Object> params = new HashMap<>();
        params.put("total",summary.getTestsFoundCount());
        params.put("success",summary.getTestsSucceededCount());
        params.put("fail",summary.getTestsFailedCount());
        params.put("date", FunctionUtil.getDate("YYYY-MM-dd HH:mm:DD"));

        //回调-润工作反馈
        if(send_flag){
            HttpUtil httpUtil = new HttpUtil();
            RequestSpecification requestSpecification = httpUtil.getRequestSpecification();
            requestSpecification.headers("content-type","application/json");
            httpUtil.setBody(requestSpecification, JSONObject.parseObject(content),params,null);
            httpUtil.send(requestSpecification,"post",run_work_web_hook_url);
        }

    }




}

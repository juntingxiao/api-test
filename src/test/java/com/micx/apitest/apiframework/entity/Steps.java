package com.micx.apitest.apiframework.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.micx.apitest.apiframework.apimodel.ApiModel;
import com.micx.apitest.apiframework.utils.*;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Data
public class Steps {
    //    接口obj对象
    public String interfaceName;
    //    接口提取参数
    private List<Extracts> extracts;
    //    接口断言
    private List<Asserts> asserts;

    private String name;

    //执行标记
    private String run;

    //前置和后置sql
    private List<RunSql> runSql;


    //登录加密方式
//    public String encryption;
    private Function function;

    public Map<String, Object> actionParams = new HashMap<>();

    public Map<String, Object> jsonPathParams = new HashMap<>();

    private static List<Map<String, ApiModel>> apiModels = new ArrayList<>();

    private static final String dir = "src/main/resources/api";

    //保存断言对象
    private List<Executable> assertList = new ArrayList<>();

    /**
     * 加载步骤 api 对象
     *
     * @param dir
     * @return
     */
    public static List<Map<String, ApiModel>> loadYamlDir(String dir) {
        Arrays.stream(new File(dir).list()).forEach(path -> {
            ApiModel apiModel = ApiModel.loadYaml(dir + File.separator + path);
            Map<String, ApiModel> map_api = new HashMap<>();
            map_api.put(apiModel.getName(), apiModel);
            apiModels.add(map_api);
        });
        return apiModels;
    }

    public void replaceSqlValue(List<Object> SqlValue, List<Object> actualSqlValue) {
        //替换 sqlValue 参数
        if (PatternUtil.isHasPattern(SqlValue.toString())) {
            actualSqlValue.addAll(PlaceholderUtil.getDefaultResolver().resolve(SqlValue, this.actionParams));
        } else {
            actualSqlValue.addAll(SqlValue);
        }
    }

    public void replaceBySqlValue(List<Object> SqlValue, List<Object> actualSqlValue) {
        if (null != SqlValue) {
            //替换 sqlValue 参数
            this.replaceSqlValue(SqlValue, actualSqlValue);
            log.info("替换前置sql参数后 actualSqlValue:{}", actualSqlValue);
        }
    }

    public void run() {

        //处理关键字函数加密
        FunctionUtil.getFunction(this);

        //替换实际参数的变量
        //获取到替换后的json串
        String resolve = PlaceholderUtil.getDefaultResolver()
                .resolveByMapReturnAsStr(JSON.toJSONString(this.actionParams),
                        ThreadUtil.getTestCase().getGlobal_map());

        this.actionParams.putAll(JSONObject.parseObject(resolve, Map.class));
        log.info("替换实际参数：{}", this.actionParams);

        //前置sql
        if (!ObjectUtils.isEmpty(this.runSql)) {
            String env = ThreadUtil.getTestCase().getEnvironment().getEnv();
            this.runSql.forEach(x -> {
                log.info("前置sql:{}", x.getBeforeSql());
                List<Object> beforeSqlValue = x.getBeforeSqlValue();
                List<Object> actualBeforeSqlValue = x.getActualBeforeSqlValue();
                this.replaceBySqlValue(beforeSqlValue, actualBeforeSqlValue);
                //执行sql把结果存到当前实例
                this.actionParams.putAll(x.runBeforeAndAfterSql(env,x.getBeforeSql(), actualBeforeSqlValue));
            });
        }


        Allure.step(this.name);

        log.info("==================执行用例步骤开始================");
        log.info("步骤名称：{}",this.name);
        //执行用例步骤
        Response response = null;
        for (Map<String, ApiModel> apiModel : apiModels) {
            ApiModel model = apiModel.get(this.interfaceName);
            if (!ObjectUtils.isEmpty(model)) {
                response = model.run(this.actionParams, this.jsonPathParams);
                log.info("接口响应：{}", response.asString());
            }
        }

        //提取
        if (response.statusCode() == 200) {
            String responseBody = response.asString();

            AtomicReference<Object> json_value = new AtomicReference<>();
            if (null != this.extracts) {
                extracts.forEach(extract -> {
                    json_value.set(ExtractsUtil.extractByJson(responseBody, extract.getValue()));
                    if ("global".equalsIgnoreCase(extract.getSave())) {
//                        TestCase.global_map.put(extract.getKey(), json_value.get());
                        ThreadUtil.getTestCase().getGlobal_map().put(extract.getKey(), json_value.get());
                    } else {
                        this.actionParams.put(extract.getKey(), json_value.get());
                    }
                });
            }
            log.info("actionParams参数:{}", this.actionParams);
            log.info("global_map参数:{}", ThreadUtil.getTestCase().getGlobal_map());
        } else {
            log.error("接口响应异常：{}", response.asString());
            throw new RuntimeException("接口响应异常");
        }

        //后置sql
        if (!ObjectUtils.isEmpty(this.runSql)) {
            String env = ThreadUtil.getTestCase().getEnvironment().getEnv();
            this.runSql.forEach(x -> {
                log.info("后置sql:{}", x.getAfterSql());
                List<Object> afterSqlValue = x.getAfterSqlValue();
                List<Object> actualAfterSqlValue = x.getActualAfterSqlValue();
                this.replaceBySqlValue(afterSqlValue, actualAfterSqlValue);
                //执行sql把结果存到当前实例
                this.actionParams.putAll(x.runBeforeAndAfterSql(env,x.getAfterSql(), actualAfterSqlValue));
            });
        }


        //断言
        if (null != this.asserts) {
            String finalResponseBody = response.asString();
            asserts.forEach(x -> {
                log.info("actual:{}", x.getActual());
                log.info("expect:{}", x.getExpect());
                Object actual = "";
                if ("eq".equalsIgnoreCase(x.getExpression())) {
                    actual = ExtractsUtil.extractByJson(finalResponseBody, x.getActual().toString());
                    Object finalActual = actual;
                    assertList.add(() -> {
                        Assertions.assertEquals(x.getExpect(), finalActual, x.getMessage());
                    });
//                    assertList.add(() -> {
//                        assertThat(x.getMessage(), finalActual.equals(x.getValue()));
//                    });
                }
            });
            Assertions.assertAll("断言错误", assertList.stream());

        }
        log.info("==================执行用例步骤结束================");

    }


//    /**
//     * 内置函数处理
//     *
//     * @param functionStr
//     * @param params
//     */
//    private void getFunction(String functionStr, Map<String, Object> params) {
//        if ("base64".equalsIgnoreCase(functionStr)) {
//            String username = params.get("username").toString();
//            String password = params.get("password").toString();
//            log.info("需要【{}】处理的账号：{}","base64",username);
//            String base64 = FunctionUtil.base64(username, password);
//            log.info("Case为：{}-{}，获取到的base64的值：{}",this.name,this.interfaceName,base64);
//            ThreadUtil.getTestCase().getGlobal_map().put("base64", base64);
//
//        }
//    }

}

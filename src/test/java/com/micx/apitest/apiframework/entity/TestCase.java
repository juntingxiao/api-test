package com.micx.apitest.apiframework.entity;

import com.micx.apitest.apiframework.utils.YamlUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Data
public class TestCase {

    public String name;
    public List<Steps> steps;
    public String run;
    public String desc;

    //测试汇总对象
    private TestSummary testSummary;

    //环境
    private Environment environment;

    //引用case 名字
    public String referenceCaseName;

    //保存已开启的测试用例
    public static List<TestCase> testCases = new ArrayList<>();

    //保存所有测试用例，方便后面通过名字查询
    public static Map<String, TestCase> testCasesMap = new HashMap<>();

    //每个cases的全局数据
//    public static Map<String, Object> global_map = new HashMap<>();
    public Map<String, Object> global_map;

    //保存已开启的测试步骤
    public List<Steps> newSteps;

    public TestSummary getTestSummary() {
        if(null != this.testSummary){
            return this.testSummary;
        }
        return new TestSummary();
    }

    public Map<String, Object> getGlobal_map() {
        if(null != this.global_map){
            return this.global_map;
        }
        global_map = new HashMap<>();
        return global_map;
    }

    /**
     * 加载用例
     * @param dir
     * @return
     */
    public static List<TestCase> loadYamlList(String dir) {
        Arrays.stream(new File(dir).list()).forEach(path -> {
            TestCase testCase = YamlUtil.readYamlByPath(dir + File.separator + path, TestCase.class);
            if ("on".equalsIgnoreCase(testCase.getRun())) {
                testCases.add(testCase);
            }
            testCasesMap.put(testCase.getName(), testCase);

        });
        return testCases;
    }

    /**
     * 过滤要执行的步骤
     * @return
     */
    public List<Steps> getRunSteps() {
        newSteps = this.steps.parallelStream()
                .filter(step -> "on".equalsIgnoreCase(step.getRun())).collect(Collectors.toList());
        return newSteps;
    }

    public TestCase run() {
        //筛选出开启执行的步骤
        this.getRunSteps();
        this.newSteps.forEach(step -> {
            log.info("==========================开始执行用例=============================");
            log.info("开始执行用例名称：{}，描述：{}",this.name,this.desc);
            if(null == this.environment){
                throw new RuntimeException("用例缺少：environment的配置");
            }
            log.info("用例执行域名：{}，环境：{}",this.environment.getBaseUrl(),this.environment.getEnv());
            step.run();
            log.info("==========================结束执行用例=============================");
        });
        return this;
    }

    /**
     * 获取前置case
     * @return
     */
    public TestCase runBeforeCase() {
        TestCase cases = this.testCasesMap.get(this.referenceCaseName);
        if (null != cases) {
            return cases;
        }
        return this;
    }

    /**
     * 根据名字找到case
     * @param caseName
     * @return
     */
    public TestCase getCase(String caseName){
        TestCase cases = this.testCasesMap.get(caseName);
        return cases;
    }


}

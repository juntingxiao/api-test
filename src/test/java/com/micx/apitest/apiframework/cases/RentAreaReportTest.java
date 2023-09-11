package com.micx.apitest.apiframework.cases;

import com.micx.apitest.apiframework.entity.Steps;
import com.micx.apitest.apiframework.entity.TestCase;
import com.micx.apitest.apiframework.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;


/**
 * 租区报事-员工代报
 */
//@SpringBootTest
@Slf4j
public class RentAreaReportTest {

    //存放case的目录
    private static final String case_dir = "src/main/resources/testcases";

    //存放api对象的目录
    private static final String api_dir = "src/main/resources/api";

    public static List<TestCase> testCases;

    public static final Boolean sendFlag = true;

    static int success = 0;
    static int fail = 0;
    static int total = 0;

//    @BeforeAll
//    public static void init() {
//        testCases = TestCase.loadYaml List(case_dir);
//        Steps.loadYamlDir(api_dir);
//    }

    /**
     * 获取测试用例
     *
     * @return
     */
    static Stream<TestCase> getCases() {
        testCases = TestCase.loadYamlList(case_dir);
        Steps.loadYamlDir(api_dir);
        return testCases.stream();
    }


    @ParameterizedTest
    @MethodSource("getCases")
    public void test_Case(TestCase testCase) {
        //把当前测试用例放到ThreadLocal，做线程隔离使用
        ThreadUtil.setTestCase(testCase);
        //获取前置依赖case
        TestCase beforeCase = testCase.runBeforeCase();
        //判断前置依赖case 是否 和 当前的case是一致  不一致则执行前置case再运行当前case
        if (!testCase.getName().equals(beforeCase.getName())) {
            beforeCase.run().getCase(testCase.getName()).run();
        } else {
            testCase.run();
        }
    }

//    @AfterAll
//    public static void after() {
//        if (sendFlag) {
//            total += ThreadUtil.getTestCase().getTestSummary().getTotal();
//            success += ThreadUtil.getTestCase().getTestSummary().getSuccess();
//            fail += ThreadUtil.getTestCase().getTestSummary().getFail();
//        }
//        log.info("总用例数：{}",total);
//        log.info("成功：{}",success);
//        log.info("失败：{}",fail);
//    }


//    @Test
//    public void test_login() {
//        // 加parallelStream() 并发
//        testCases.parallelStream().forEach(testCase -> {
//            ThreadUtil.setTestCase(testCase);
//            //获取前置依赖case
//            TestCase beforeCase = testCase.runBeforeCase();
//            //判断前置依赖case 是否 和 当前的case是一致  不一致则执行前置case再运行当前case
//            if (!testCase.getName().equals(beforeCase.getName())) {
//                beforeCase.run().getCase(testCase.getName()).run();
//            } else {
//                testCase.run();
//            }
//        });
//    }

}

package com.micx.apitest.apiframework.utils;

import com.micx.apitest.apiframework.entity.Function;
import com.micx.apitest.apiframework.entity.FunctionEnum;
import com.micx.apitest.apiframework.entity.Steps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * 函数
 */
@Slf4j
public class FunctionUtil {

    //用于产生随机字符串【字符集】
    public static final String Charts = "0123456789qwertyuiopasdfghjklzxcvbnm";


    public static String getDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }


    /**
     * 登录base64
     * @param mobile
     * @param passwod
     * @return
     */
    public static String base64(String mobile,String passwod){
        String byteMobilePassword = mobile + ":" + passwod;
        String base64MobilePassword = Base64.getEncoder().encodeToString(byteMobilePassword.getBytes());
        return base64MobilePassword;
    }

    /**
     * 获取随机字符串
     * @param count
     * @return
     */
    public static String randomString(int count){
        return RandomStringUtils.random(count,Charts);
    }


    /**
     * 内置函数处理
     *
     * @param steps
     */
    public static void getFunction(Steps steps){
        Function function = steps.getFunction();
        if(ObjectUtils.isEmpty(function)){
            return;
        }
        String functionName = function.getFunctionName();
        if(StringUtils.isEmpty(functionName)){
            return;
        }

        if (FunctionEnum.BASE64.getFunctionName().equalsIgnoreCase(functionName)) {
            Map<String, Object> actionParams = steps.getActionParams();
            String username = actionParams.get("username").toString();
            String password = actionParams.get("password").toString();
            log.info("需要【{}】处理的账号：{}","base64",username);
            String base64 = base64(username, password);
            log.info("Case为：{}-{}，获取到的base64的值：{}",steps.getName(),steps.getInterfaceName(),base64);
            ThreadUtil.getTestCase().getGlobal_map().put("base64", base64);
        }else if(FunctionEnum.RANDOMSTRING.getFunctionName().equalsIgnoreCase(functionName)){
            long currentTimeMillis = System.currentTimeMillis();
            String position = String.valueOf(currentTimeMillis).substring(5, 6);
            String randomStringValue = randomString(Integer.valueOf(position));
            ThreadUtil.getTestCase().getGlobal_map().put("randomString", randomStringValue);
        }
    }

    public static void main(String[] args) {
        String date = FunctionUtil.getDate("YYYY-MM-dd HH:mm:DD");
        System.out.println(date);
    }




}

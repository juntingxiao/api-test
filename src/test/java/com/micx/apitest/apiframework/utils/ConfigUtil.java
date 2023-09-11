package com.micx.apitest.apiframework.utils;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
public class ConfigUtil {

    public static String readConf(String pathName,String sectionName,String optionName){
        String baseUrl="";
        try {
            // 步骤1：创建ini配置文件
            Ini ini = new Ini();

            // 步骤2：读取ini配置文件
            ini.load(new File(pathName));

            // 步骤3：获取配置项的值
            baseUrl = ini.get(sectionName,optionName);
            System.out.println(baseUrl);

            // 步骤4：修改配置项的值
//            ini.put("section1", "property1", "new value");

            // 步骤5：保存ini配置文件
//            ini.store(new File("config.ini"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return baseUrl;
    }

    public static void main(String[] args) {

    readConf("src/main/resources/conf.ini","servers","test");
    }
}

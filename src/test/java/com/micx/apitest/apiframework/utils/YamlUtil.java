package com.micx.apitest.apiframework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;

/**
 * 读取yaml文件
 */
public class YamlUtil {

    /**
     * 根据路径路径加载所有yaml文件
     * @param path
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T readYamlByPath(String path,Class<T> clazz){
        ObjectMapper mapper =  new ObjectMapper ( new YAMLFactory() );
        try {
            return mapper.readValue(
                    new File(path),
                    clazz
            );
        } catch (IOException e) {
            System.out.println("路径不存在");
            System.out.println(e);
            return null;
        }
    }


    /**
     * 读取指定路径yaml文件
     * @param fileName
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T readYamlByFile(String fileName,Class<T> clazz){
        ObjectMapper mapper =  new ObjectMapper ( new YAMLFactory() );
        try {
            return mapper.readValue(
                    YamlUtil.class.getResourceAsStream (fileName),
                    clazz
            );
        } catch (IOException e) {
            System.out.println("yaml文件不存在或者类型错误");
            System.out.println(e);
            return null;
        }
    }
}

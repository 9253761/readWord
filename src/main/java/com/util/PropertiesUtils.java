/*
 * Copyright (c) 2016-2018 fafa.com.cn. All Rights Reserved.
 */

package com.util;
import java.io.IOException;
import java.util.Properties;

/**
 * 这是读取properties的工具类
 *编写一个读取properties属性文件的方法类PropertiesUtils
 * @author Cll
 * @date 2018/11/13 9:42
 * @since 1.0.0
 */

public class PropertiesUtils {
    static Properties property = new Properties();
    public static boolean loadFile(String fileName){
        try {
            property.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    //得到配置文件的某个值
    public static String getPropertyValue(String key){
        return property.getProperty(key);
    }
}

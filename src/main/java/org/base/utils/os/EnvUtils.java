package org.base.utils.os;

import java.util.Map;

/**
 * @Auther: jinxiang
 * @Date: 2018/11/7 14:32
 * @Description:
 */
public class EnvUtils {


    public static Map<String, String> getAllEnv() {
        return System.getenv();

    }

    public static String getEnv(String key) {
        return System.getenv(key);
    }
    public static void setEnv(String key,String value){
        System.setProperty(key,value);
    }

    public static void main(String args[]) {
        setEnv("key","value");
        Map<String, String> allEnv = getAllEnv();
        allEnv.forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
        System.out.println(getEnv("key"));
    }
}

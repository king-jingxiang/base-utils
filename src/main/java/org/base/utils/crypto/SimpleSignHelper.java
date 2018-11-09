package org.base.utils.crypto;


import com.google.common.base.Strings;

public class SimpleSignHelper {

    private final static String key = "Sni(wEv]";

    public static String sign(String content) {
        if (Strings.isNullOrEmpty(content)) {
            return null;
        }
        String str = content.concat("_").concat(key);

        return MD5.encrypt(str).substring(0, 8);
    }

    public static boolean checkSign(String content, String sign) {
        if (Strings.isNullOrEmpty(content)) {
            return false;
        }
        String str = content.concat("_").concat(key);

        return MD5.encrypt(str).substring(0, 8).equals(sign);
    }
}

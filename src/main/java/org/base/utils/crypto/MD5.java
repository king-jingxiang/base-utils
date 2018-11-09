package org.base.utils.crypto;

import com.google.common.base.Strings;

import java.nio.charset.Charset;
import java.security.MessageDigest;

public class MD5 {


    private static final String DFT_ENC_NAME = "MD5";

    private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * 加密字符串
     *
     * @param content 待加密字符串
     * @return 加密后结果
     * @see #encrypt(String, Charset)
     */
    public static String encrypt(String content) {
        return encrypt2(content, DEFAULT_CHARSET);
    }

    /**
     * 加密字符串
     *
     * @param content 待加密字符串
     * @param charset 字符串字符编码
     * @return 加密后结果
     */
    public static String encrypt(String content, Charset charset) {
        if (charset == null) charset = DEFAULT_CHARSET;
        return encrypt2(content, charset);
    }

    /**
     * 加密字节数组
     *
     * @param bytes 待加密字节数组
     * @return 加密后结果
     */
    public static String encrypt(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            throw new IllegalArgumentException("Empty MD5 source bytes.");
        try {
            MessageDigest md = MessageDigest.getInstance(DFT_ENC_NAME);
            md.update(bytes);
            return bytes2Hex(md.digest());
        } catch (Exception e) {
            throw new RuntimeException("MD5 bytes Failed.", e);
        }
    }

    /**
     * 使用指定的关键字加密
     *
     * @param content 待加密字符串
     * @param key     加密密钥
     * @return 加密后的MD5值
     */
    public static String encryptWithKey(String content, String key) {
        return encryptWithKey(content.getBytes(DEFAULT_CHARSET), key);
    }

    /**
     * 使用指定的关键字加密
     *
     * @param bytes 待加密字节数组
     * @param key   加密密钥
     * @return 加密后的MD5值
     */
    public static String encryptWithKey(byte[] bytes, String key) {
        byte[] kbytes = key.getBytes();
        byte[] newBytes = new byte[bytes.length + kbytes.length];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        System.arraycopy(kbytes, 0, newBytes, bytes.length, kbytes.length);
        return encrypt(newBytes);
    }

    private static String encrypt2(String content, Charset charset) {

        if (Strings.isNullOrEmpty(content))
            throw new IllegalArgumentException("Empty MD5 source content.");
        try {
            MessageDigest md = MessageDigest.getInstance(DFT_ENC_NAME);
            md.update(content.getBytes(charset));
            return bytes2Hex(md.digest());
        } catch (Exception e) {
            throw new RuntimeException("MD5 Failed.[" + content + "]", e);
        }
    }

    /**
     * 将byte数组转换为表示16进制值的字符串，与{@link #hex2Bytes(String)}互为逆转过程
     *
     * @param bts 字节数组
     * @return 十六进制字符串
     */
    @SuppressWarnings("javadoc")
    protected static String bytes2Hex(byte[] bts) {
        String tmp;

        StringBuilder ret = new StringBuilder(bts.length << 1);
        for (int i = 0; i < bts.length; i++) {
            tmp = Integer.toHexString(bts[i] & 0xFF);
            if (tmp.length() == 1) ret.append("0");
            ret.append(tmp);
        }

        return ret.toString();
    }
}

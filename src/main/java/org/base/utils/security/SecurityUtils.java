package org.base.utils.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 旧的登录userid加密算法
 */
@SuppressWarnings("restriction")
public class SecurityUtils {
	private final static String KEY = "ET9C34qyTIioyt";

	public static String desEncrypt(String plainText) throws Exception {
		String encryptStr = null;
		try {
			DESKeySpec keySpec = new DESKeySpec(KEY.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("des");
			SecretKey secretKey = keyFactory.generateSecret(keySpec);

			Cipher cipher = Cipher.getInstance("des");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
			byte[] cipherData = cipher.doFinal(plainText.getBytes());
            encryptStr = Base64.getEncoder().encodeToString(cipherData);

        } catch (Exception e) {
			throw new Exception("加密失败");
		}
		return encryptStr;
	}

	public static String desDecrypt(String encryptStr) throws Exception {
		String decryptStr = null;
		try {
			DESKeySpec keySpec = new DESKeySpec(KEY.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("des");
			SecretKey secretKey = keyFactory.generateSecret(keySpec);

			Cipher cipher = Cipher.getInstance("des");

			cipher.init(Cipher.DECRYPT_MODE, secretKey, new SecureRandom());
            byte[] plainData = Base64.getDecoder().decode(encryptStr);
            decryptStr = new String(plainData);
		} catch (Exception e) {
			throw new Exception("解密失败");
		}
		return decryptStr;
	}

	public static void main(String[] args) throws Exception {
		String id = SecurityUtils.desEncrypt("123123123123");
		System.out.println(id);
		System.out.println(SecurityUtils.desDecrypt(id));
		System.out.println(SecurityUtils.desDecrypt("t1WpsmmxvHQ="));
	}
}

package com.fy.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	
	private MD5Util() {}

	public static String getMD5String(String password) {
		byte[] bytes = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			/* 更新摘要 */
			digest.update(password.getBytes());
			/* 再通过执行诸如填充之类的最终操作完成哈希计算。在调用此方法之后，摘要被重置。 */
			bytes = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		StringBuilder builder = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			/**
			 * 0xFF默认是整形，一个byte跟0xFF相与会先将那个byte转化成整形运算
			 */
			if ((b & 0xFF) < 0x10) { // 如果为1位 前面补个0
				builder.append("0");
			}
			builder.append(Integer.toHexString(b & 0xFF));
		}
		return builder.toString();
	}
}

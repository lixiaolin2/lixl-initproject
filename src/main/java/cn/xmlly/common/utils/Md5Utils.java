package cn.xmlly.common.utils;

import org.springframework.util.DigestUtils;

public class Md5Utils {

	public static String getMd5(String key){
		return DigestUtils.md5DigestAsHex(key.getBytes());
	}
	
	public static String getMd5(String salt,String key){
		return DigestUtils.md5DigestAsHex((salt+key).getBytes());
	}
}

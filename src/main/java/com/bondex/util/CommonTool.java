package com.bondex.util;

import org.apache.commons.lang3.StringUtils;

public class CommonTool {
	


	/**
	 * * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
	 * @param value
	 * @param separatorChar
	 * @return
	 */
	public static String getSplitResult(String value,String separatorChar){
		if(toCheckConstains(value,separatorChar, 0)){
			String string = StringUtils.substringAfter(value, separatorChar);
			return string;
		}
		return value;
	}
	
	
	//判断字符串是否存在
	public static Boolean toCheckConstains(String str,String s,int k){
		//存在为true 不存在false
		return StringUtils.indexOf(str,s,k)!=-1;
		
	}
	
	/**
	 * 拼接 主单号 18068750264 改为 180-68750264
	 * @param value
	 * @return
	 */
	public static String getMawb(String value){
		String s1 = value;
		String s2 = "-";
		int i = 3;// 插入到第三位
		String newString = s1.substring(0, i) + s2 + s1.substring(i, s1.length()); //得到主单号
		return newString;
	}
	
	
	
}

package com.bondex.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionUtils {

	private final static String SEPARATE = " ";
    private final static String JOIN_FLAG = " ";
	 /**
     * 字符串转换为字符串数组
     *
     * @param content 字符串
     * @return 字符串数组
     */
    public static String[] string2Array(String content) {
        if (content == null || content.length() == 0) {
            return null;
        }
        return content.split(SEPARATE);
    }

    /**
     * 字符串数组转换为字符串
     *
     * @param stringArray 字符串数组
     * @return 字符串
     */
    public static String array2String(String [] stringArray) {
        if (stringArray == null) {
            return null;
        }
        return String.join(JOIN_FLAG, stringArray);
    }

    /**
     * 字符串数组转换为字符串List
     *
     * @param stringArray 字符串数组
     * @return 字符串List
     */
    public static List<String> array2List(String [] stringArray) {
        if (stringArray == null) {
            return null;
        }
        return Arrays.asList(stringArray);
    }

    /**
     * 字符串List转换为字符串数组
     *
     * @param stringList 字符串List
     * @return 字符串数组
     */
    public static String[] list2Array(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        return stringList.toArray(new String[0]);
    }
    
    /**
     * 字符串Set转换为字符串数组
     *
     * @param stringList 字符串List
     * @return 字符串数组
     */
    public static String[] set2Array(Set<String> stringSet) {
    	if (stringSet == null) {
    		return null;
    	}
    	return stringSet.toArray(new String[0]);
    }

    /**
     * 字符串List转换为字符串Set
     *
     * @param stringList 字符串List
     * @return 字符串Set
     */
    public static Set<String> list2Set(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        return new HashSet<String>(stringList);
    }

    /**
     * 字符串Set转换为字符串List
     *
     * @param stringSet 字符串Set
     * @return 字符串List
     */
    public static List<String> set2List(Set<String> stringSet) {
        if (stringSet == null) {
            return null;
        }
        return new ArrayList<>(stringSet);
    }
    
    /**
     * 字符串List转换为字符串Set
     *
     * @param stringList 字符串List
     * @return 字符串Set
     */
    public static <T> Set<T> ListToNewSet(List<T> list) {
        if (list == null) {
            return null;
        }
        return new HashSet<T>(list);
    }
    
    /**
     * 字符串Set转换为字符串List
     *
     * @param stringSet 字符串Set
     * @return 字符串List
     */
    public static <T>List<T>  SetToNewList(Set<T> set) {
    	if (set == null) {
    		return null;
    	}
    	return new ArrayList<T>(set);
    }
    
    /**
    * List和Set的转化(Set转化成List)
    */
    public static <T> List<T> SetToList(Set<T> set) {
        List<T> list = new ArrayList<T>();
        list.addAll(set);// 转换核心
        return list;
    }
    
    /**
     * List和Set的转化(List转化成Set)
     */
    public static <T> Set<T> ListToSet(List<T> list) {
    	Set<T> set = new HashSet<T>();
    	set.addAll(list);// 转换核心
    	return set;
    }
	
}

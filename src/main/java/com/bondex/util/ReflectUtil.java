package com.bondex.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射工具类
 * @author Qianli
 * 
 * 2019年8月30日 下午2:27:17
 */
public class ReflectUtil {

	/**
	 * 获取指定字段名称查找在class中的对应的Field对象(包括查找父类)
	 * 
	 * @param clazz 指定的class
	 * @param fieldName 字段名称
	 * @return Field对象
	 */
	public static Field getClassField(Class clazz, String fieldName) {
		if( Object.class.getName().equals(clazz.getName())) {
			return null;
		}
		Field []declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}

		Class superClass = clazz.getSuperclass();
		if(superClass != null) {// 简单的递归一下
			return getClassField(superClass, fieldName);
		}
		return null;
	}  
	
	/*
	 * 获取某个类中所有的字段及其父类字段对象
	 * 
	 */
	public static Field[] getAllFields(Object object){
		  Class clazz = object.getClass();
		  List<Field> fieldList = new ArrayList<>();
		  while (clazz != null){
		    fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
		    clazz = clazz.getSuperclass();
		  }
		  Field[] fields = new Field[fieldList.size()];
		  fieldList.toArray(fields);
		  return fields;
		}
	
	 /**
		 * 反射方法，通过get方法获取对象属性值value
		 * 
		 * @param owner
		 * @param fieldname
		 * @param args
		 * @return
		 * @throws Exception
		 */
		public static Object invokeGetMethod(Object owner, String fieldName){
			try {
				StringBuffer sb = new StringBuffer();
				sb.append("get").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));  
				String methodName = sb.toString();
				Class<?> ownerClass = owner.getClass();
				Method method = ownerClass.getMethod(methodName);
				return method.invoke(owner);
				
			} catch (Exception e) {
				System.err.println("获取字段值["+fieldName+"]错误,原因："+e.getMessage());
			}
			return null;
		}
		
		/** 
	     * java反射bean的set方法    可以使用该方法 BeanUtils.setProperty
	     *  
	     * @param objectClass 
	     * @param fieldName 
	     * @return 
	     */  
	    @SuppressWarnings("unchecked")  
	    public static void invokeSetMethod(Object o, String fieldName,Object value) {  
	        try {  
	            Class[] parameterTypes = new Class[1];  
	            Class objectClass = o.getClass();
	            Field field = getClassField(objectClass, fieldName);
	            parameterTypes[0] = field.getType();  
	            StringBuffer sb = new StringBuffer();  
	            sb.append("set").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));  
	            Method method = objectClass.getMethod(sb.toString(), parameterTypes);  
	            //类型转换
	            value = convertValType(value, parameterTypes[0]);
	            method.invoke(o, new Object[] { value });  //将所有的值转为Object类型
	        } catch (Exception e) {
	        	System.err.println("解析数据封装字段["+fieldName+"]类型错误,值:["+value+"]原因："+e.getMessage());
	        	e.printStackTrace();
	        }  
	    }  
	    
	    
	    /**
		 * 将Object类型的值，转换成bean对象属性里对应的类型值
		 * 
		 * @param value Object对象值
		 * @param fieldTypeClass 属性的类型
		 * @return 转换后的值
		 */
		private static Object convertValType(Object value, Class fieldTypeClass) {
			Object retVal = null;
			if(null==value){
				return value;
			}else  if(Long.class.getName().equals(fieldTypeClass.getName())
					|| long.class.getName().equals(fieldTypeClass.getName())) {
				 if ("".equals(value)) {
						value='0';
				 }
				retVal = Long.parseLong(value.toString());
			} else if(Integer.class.getName().equals(fieldTypeClass.getName())
					|| int.class.getName().equals(fieldTypeClass.getName())) {
				if ("".equals(value)) {
					value='0';
				}
				retVal = Integer.parseInt(value.toString());
			} else if(Float.class.getName().equals(fieldTypeClass.getName())
					|| float.class.getName().equals(fieldTypeClass.getName())) {
				if ("".equals(value)) {
					value='0';
				}
				retVal = Float.parseFloat(value.toString());
			} else if(Double.class.getName().equals(fieldTypeClass.getName())
					|| double.class.getName().equals(fieldTypeClass.getName())) {
				if ("".equals(value)) {
					value='0';
				}
				retVal = Double.parseDouble(value.toString());
			} else if(BigDecimal.class.getName().equals(fieldTypeClass.getName())){
				if ("".equals(value)) {
					value='0';
				}
				retVal =new BigDecimal(value.toString());
			}else if(String.class.getName().equals(fieldTypeClass.getName())){
				return value;
			}else{
				retVal = value;
			}
			return retVal;
		}

	
}

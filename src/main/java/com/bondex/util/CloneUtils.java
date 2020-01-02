package com.bondex.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class CloneUtils {
	 
	/**
	 * 是使用序列化的方式来实现对象的深拷贝，但是前提是，对象必须是实现了Serializable接口才可以
	 * @param obj
	 * @return
	 */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T obj){
         
        T clonedObj = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
             
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();
            ois.close();
             
        }catch (Exception e){
        	
        	e.printStackTrace();
        }
         
        return clonedObj;
        
    }	
  
    
    /**
	 * 采用对象的序列化完成对象的深克隆
	 * 是使用序列化的方式来实现对象的深拷贝，但是前提是，对象必须是实现了Serializable接口才可以
	 * @autor:chenssy
	 * @date:2014年8月9日
	 *
	 * @param obj
	 * 			待克隆的对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T cloneObject(T obj) {
		T cloneObj = null;
		try {
			// 写入字节流
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream obs = new ObjectOutputStream(out);
			obs.writeObject(obj);
			obs.close();

			// 分配内存，写入原始对象，生成新对象
			ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(ios);
			// 返回生成的新对象
			cloneObj = (T) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cloneObj;
	}
	
	/**
	 * 利用序列化完成集合的深克隆
	 * @autor:chenssy
	 * @date:2014年8月9日
	 *
	 * @param collection
	 * 					待克隆的集合
	 * @return
	 * @throws ClassNotFoundException
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> cloneCollection(Collection<T> collection) throws ClassNotFoundException, IOException{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
	    ObjectOutputStream out = new ObjectOutputStream(byteOut);  
	    out.writeObject(collection);
	    out.close();
	  
	    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
	    ObjectInputStream in = new ObjectInputStream(byteIn);  
	    Collection<T> dest = (Collection<T>) in.readObject();  
	    in.close();
	    return dest;  
	}
    
  
    
}
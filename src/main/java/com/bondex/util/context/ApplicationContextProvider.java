package com.bondex.util.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * 创建一个实体类并实现ApplicationContextAware接口，重写接口内的setApplicationContext方法来完成获取ApplicationContext实例的方法
 * @author Qianli
 * 
 * 2018年8月14日 下午7:02:20
 */
@Component(value="applicationContextProvider") //必须加上该注解
public class ApplicationContextProvider implements ApplicationContextAware {

		/**
	     * 上下文对象实例
	     */
	    private static ApplicationContext applicationContext;

	    @Override
	    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	        this.applicationContext = applicationContext;
	    }

	    /**
	     * 获取applicationContext
	     * @return
	     */
	    public static ApplicationContext getApplicationContext() {
	        return applicationContext;
	    }

	    /**
	     * 通过name获取 Bean.
	     * @param name
	     * @return
	     */
	    public static Object getBean(String name){
	    	try {
	    		return getApplicationContext().getBean(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return null;
	    }

	    /**
	     * 通过class获取Bean.
	     * @param clazz
	     * @param <T>
	     * @return
	     */
	    public static <T> T getBean(Class<T> clazz){
	        return getApplicationContext().getBean(clazz);
	    }

	    /**
	     * 通过name,以及Clazz返回指定的Bean
	     * @param name
	     * @param clazz
	     * @param <T>
	     * @return
	     */
	    public static <T> T getBean(String name,Class<T> clazz){
	        return getApplicationContext().getBean(name, clazz);
	    }
	    
	    /// 获取当前环境
	    public static String getActiveProfile() {
	        return getApplicationContext().getEnvironment().getActiveProfiles()[0];
	    }

	    private static void assertApplicationContext() {
	        if (ApplicationContextProvider.applicationContext == null) {
	            throw new RuntimeException("applicaitonContext属性为null,请检查是否注入了ApplicationContextProvider!");
	        }
	    }

}

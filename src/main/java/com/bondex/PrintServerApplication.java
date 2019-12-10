package com.bondex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


/**
 * @author admin
 */
@SpringBootApplication
@EnableCaching
public class PrintServerApplication {
	
	/**
	 * org.slf4j.Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PrintServerApplication.class);

    public static void main(String[] args) {

    	// 开始时间
 	    long start = System.currentTimeMillis();
 	   
        SpringApplication.run(PrintServerApplication.class, args);
        
        // 结束时间
	    Long end = System.currentTimeMillis();
	    logger.debug("标签打印系统服务项目已启动... 耗时:{}ms。",(end - start));
        
        
        
    }

}

package com.bondex.config.jdbc;

import java.io.FileInputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;

@Configuration
/**
 * @PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true)
 * jdbc.driver=com.mysql.jdbc.Driver
	jdbc.url=jdbc:mysql://localhost:3306/testdb
	jdbc.username=user7
	jdbc.password=s$cret
 */
public class DataSourceConfig {

    @Autowired
    private Environment env;

    //spring 方式读取properties注入数据源
  /*  @Bean
    public DataSource dataSource() {

        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driver"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.username"));
        dataSource.setPassword(env.getProperty("jdbc.password"));

        return dataSource;
    }*/
    
    /**
     * springBoot 方式 注入数据源
     * @return
     */
  /*  @Bean
    @ConfigurationProperties(prefix = "mysql.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }*/

    
    // @ConfigurationProperties() 将配置文件中的连接池配置添加到数据源
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource dataSource(){
        return new DruidDataSource(); //druid注入dataSource
    }
    
    //使用spring容器中的dataSource 注入jdbcTemplate 模板
    @Bean
    public JdbcTemplate jdbcTemplate() {

    	JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(dataSource());
        return template;
    }
    
    /**
     * template扩展
     * @return
     */
    @Bean
    public JdbcTemplateSupport jdbcTemplateSupport() {
    	JdbcTemplateSupport template = new JdbcTemplateSupport(dataSource());
    	return template;
    }
    /**
     * template扩展
     * @return
     */
    @Bean
    public NamedParameterJdbcTemplateSupport namedParameterJdbcTemplateSupport() {
    	NamedParameterJdbcTemplateSupport template = new NamedParameterJdbcTemplateSupport(dataSource());
    	return template;
    }
    
    
    
    
  /* 
   * 使用properties创建驱动注入template 模板
    @SuppressWarnings("unchecked")
	@Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(){
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
     try {
    	 Properties prop = new Properties();
		prop.load(new FileInputStream("src/main/resources/db.properties"));
		SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClass(((Class<Driver>) Class.forName(prop.getProperty("jdbc.driver"))));
		ds.setUrl(prop.getProperty("jdbc.url"));
		ds.setUsername(prop.getProperty("jdbc.username"));
		ds.setPassword(prop.getProperty("jdbc.password"));
		
		namedParameterJdbcTemplate= new NamedParameterJdbcTemplate(ds);
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	    return namedParameterJdbcTemplate;

    }*/
}
package com.bondex.config.swagger2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Mr.Yangxiufeng on 2017/6/26.
 * Time:18:05
 * ProjectName:Common-admin
 */
//@EnableWebMvc   //不能使用这个注解，会修改springBoot框架默认的静态文件路径 
//扫描control所在的package请修改为你control所在package
@Configuration
@EnableSwagger2 //重要
//@ComponentScan(basePackages = "com.boe.controller")
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful APIs")
                .description("Spring Boot中使用Swagger2接口说明文档：http://i.bondex.com.cn")
                .termsOfServiceUrl("http://www.baidu.com/")
                .contact(new Contact("QIANLI", "http://i.bondex.com.cn", "2282500426@qq.com"))
                .version("1.0")
                .build();
    }
    
  
}

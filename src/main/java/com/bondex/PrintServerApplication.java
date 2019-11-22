package com.bondex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PrintServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrintServerApplication.class, args);
	}

}

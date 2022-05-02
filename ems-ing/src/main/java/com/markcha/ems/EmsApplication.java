package com.markcha.ems;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmsApplication {
	@Value("${response.jpa.DB_INSERT_MSG}")
	private static String dbInsertMsg;
	public static void main(String[] args) {
		System.out.println(dbInsertMsg);
		SpringApplication.run(EmsApplication.class, args);
	}

}

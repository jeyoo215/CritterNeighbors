package com.critter.critter_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.critter.critter_backend")
public class CritterBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CritterBackendApplication.class, args);
	}
}
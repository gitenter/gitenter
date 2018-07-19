package com.gitenter.envelope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.gitenter.envelope",
		"com.gitenter.protease"})
public class EnvelopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvelopeApplication.class, args);
	}
}
